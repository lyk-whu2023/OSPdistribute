package com.shopping.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.product.dto.request.SeckillProductRequest;
import com.shopping.product.dto.response.SeckillActivityResponse;
import com.shopping.product.dto.response.SeckillProductResponse;
import com.shopping.product.entity.SeckillActivity;
import com.shopping.product.entity.SeckillProduct;
import com.shopping.product.mapper.SeckillActivityMapper;
import com.shopping.product.mapper.SeckillProductMapper;
import com.shopping.product.service.SeckillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillServiceImpl implements SeckillService {

    private final SeckillActivityMapper seckillActivityMapper;
    private final SeckillProductMapper seckillProductMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<SeckillActivityResponse> getActiveActivities() {
        log.info("查询进行中的秒杀活动");
        String cacheKey = "seckill:activities:active";
        
        // 尝试从缓存获取
        List<SeckillActivityResponse> cached = (List<SeckillActivityResponse>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("缓存命中，返回进行中的秒杀活动");
            return cached;
        }
        
        // 缓存未命中，从数据库查询
        LocalDateTime now = LocalDateTime.now();
        List<SeckillActivityResponse> activities = seckillActivityMapper.selectList(new LambdaQueryWrapper<SeckillActivity>()
                        .eq(SeckillActivity::getStatus, 1)
                        .le(SeckillActivity::getStartTime, now)
                        .ge(SeckillActivity::getEndTime, now))
                .stream()
                .map(this::convertActivityToResponse)
                .collect(Collectors.toList());
        
        // 缓存结果，60 分钟过期
        if (activities != null && !activities.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, activities, 60, TimeUnit.MINUTES);
            log.info("进行中的秒杀活动已缓存，数量：{}", activities.size());
        }
        
        return activities;
    }

    @Override
    public List<SeckillProductResponse> getSeckillProductsByActivity(Long activityId) {
        log.info("查询秒杀活动商品，activityId: {}", activityId);
        String cacheKey = "seckill:activity:" + activityId + ":products";
        
        // 尝试从缓存获取
        List<SeckillProductResponse> cached = (List<SeckillProductResponse>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("缓存命中，返回秒杀活动商品");
            return cached;
        }
        
        // 缓存未命中，从数据库查询
        List<SeckillProductResponse> products = seckillProductMapper.selectList(new LambdaQueryWrapper<SeckillProduct>()
                        .eq(SeckillProduct::getActivityId, activityId)
                        .eq(SeckillProduct::getStatus, 1))
                .stream()
                .map(this::convertProductToResponse)
                .collect(Collectors.toList());
        
        // 缓存结果，20 分钟过期
        if (products != null && !products.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, products, 20, TimeUnit.MINUTES);
            log.info("秒杀活动商品已缓存，数量：{}", products.size());
        }
        
        return products;
    }

    @Override
    public SeckillProductResponse getSeckillProduct(Long id) {
        log.info("查询秒杀商品详情，id: {}", id);
        String cacheKey = "seckill:product:" + id;
        SeckillProduct product = (SeckillProduct) redisTemplate.opsForValue().get(cacheKey);

        if (product == null) {
            log.info("缓存未命中，从数据库查询，id: {}", id);
            product = seckillProductMapper.selectById(id);
            if (product != null) {
                redisTemplate.opsForValue().set(cacheKey, product, 30, TimeUnit.MINUTES);
                log.info("秒杀商品详情已缓存，id: {}", id);
            }
        } else {
            log.info("缓存命中，id: {}", id);
        }

        return convertProductToResponse(product);
    }

    @Override
    public boolean purchaseSeckillProduct(Long seckillProductId, Long userId) {
        String stockKey = "seckill:stock:" + seckillProductId;
        String userKey = "seckill:user:" + seckillProductId + ":" + userId;

        try {
            Boolean hasPurchased = redisTemplate.opsForValue().setIfAbsent(userKey, "1", 1, TimeUnit.DAYS);
            if (hasPurchased == null || !hasPurchased) {
                log.warn("用户重复购买秒杀商品，userId: {}, seckillProductId: {}", userId, seckillProductId);
                throw new RuntimeException("每人限购一件");
            }

            Long stock = redisTemplate.opsForValue().decrement(stockKey);
            if (stock == null || stock < 0) {
                redisTemplate.delete(userKey);
                log.warn("秒杀商品库存不足，seckillProductId: {}, userId: {}", seckillProductId, userId);
                throw new RuntimeException("库存不足");
            }
            // 构造消息体：包含商品 ID、用户 ID、时间戳
            Map<String, Object> orderMessage = new HashMap<>();
            orderMessage.put("seckillProductId", seckillProductId);
            orderMessage.put("userId", userId);
            orderMessage.put("timestamp", LocalDateTime.now().toString());
            // 将消息转为 JSON 字符串
            String messageJson;
            try {
                messageJson = objectMapper.writeValueAsString(orderMessage);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.error("序列化秒杀订单消息失败，seckillProductId: {}, userId: {}, error: {}", seckillProductId, userId, e.getMessage(), e);
                throw new RuntimeException("序列化消息失败", e);
            }
            kafkaTemplate.send("seckill-order", messageJson);
            
            log.info("秒杀订单消息已发送到 Kafka，seckillProductId: {}, userId: {}", seckillProductId, userId);
            
            return true;
        } catch (Exception e) {
            log.error("秒杀购买失败，seckillProductId: {}, userId: {}, error: {}", seckillProductId, userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public SeckillProductResponse createSeckillProduct(SeckillProductRequest request) {
        SeckillProduct product = new SeckillProduct();
        BeanUtils.copyProperties(request, product);
        product.setSold(0);
        product.setStatus(1);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        seckillProductMapper.insert(product);
        
        // 初始化 Redis 库存
        String stockKey = "seckill:stock:" + product.getId();
        redisTemplate.opsForValue().set(stockKey, product.getStock());
        log.info("秒杀商品 Redis 库存已初始化，id: {}, stock: {}", product.getId(), product.getStock());
        
        return convertProductToResponse(product);
    }

    @Override
    public boolean initSeckillStock() {
        log.info("开始初始化所有秒杀商品 Redis 库存");
        List<SeckillProduct> products = seckillProductMapper.selectList(
            new LambdaQueryWrapper<SeckillProduct>().eq(SeckillProduct::getStatus, 1)
        );
        
        int count = 0;
        for (SeckillProduct product : products) {
            String stockKey = "seckill:stock:" + product.getId();
            redisTemplate.opsForValue().set(stockKey, product.getStock());
            count++;
            log.info("秒杀商品库存已初始化，id: {}, stock: {}", product.getId(), product.getStock());
        }
        
        log.info("共初始化{}个秒杀商品的 Redis 库存", count);
        return count > 0;
    }

    private SeckillProductResponse convertProductToResponse(SeckillProduct product) {
        if (product == null) {
            return null;
        }
        SeckillProductResponse response = new SeckillProductResponse();
        BeanUtils.copyProperties(product, response);

        SeckillActivity activity = seckillActivityMapper.selectById(product.getActivityId());
        if (activity != null) {
            response.setActivityName(activity.getName());
            response.setStartTime(activity.getStartTime());
            response.setEndTime(activity.getEndTime());
        }

        return response;
    }

    private SeckillActivityResponse convertActivityToResponse(SeckillActivity activity) {
        if (activity == null) {
            return null;
        }
        SeckillActivityResponse response = new SeckillActivityResponse();
        BeanUtils.copyProperties(activity, response);
        return response;
    }
}