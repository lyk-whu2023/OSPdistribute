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
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
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
    private final RedissonClient redissonClient;
    private final RBloomFilter<Long> seckillProductBloomFilter;

    private static final String SECKILL_PRODUCT_CACHE_KEY = "seckill:product:";
    private static final String SECKILL_LOCK_KEY = "seckill:lock:product:";
    private static final long LOCK_WAIT_TIME = 3;
    private static final long LOCK_LEASE_TIME = 10;

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
        
        if (!seckillProductBloomFilter.contains(id)) {
            log.info("布隆过滤器拦截，秒杀商品不存在，id: {}", id);
            return null;
        }
        
        String cacheKey = SECKILL_PRODUCT_CACHE_KEY + id;
        String lockKey = SECKILL_LOCK_KEY + id;
        
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof SeckillProduct) {
            log.info("缓存命中，id: {}", id);
            return convertProductToResponse((SeckillProduct) cached);
        }
        
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            boolean acquired = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            
            if (acquired) {
                try {
                    cached = redisTemplate.opsForValue().get(cacheKey);
                    if (cached instanceof SeckillProduct) {
                        log.info("双重检查缓存命中，id: {}", id);
                        return convertProductToResponse((SeckillProduct) cached);
                    }
                    
                    log.info("获取分布式锁成功，查询数据库，id: {}", id);
                    SeckillProduct product = seckillProductMapper.selectById(id);
                    
                    if (product != null) {
                        redisTemplate.opsForValue().set(cacheKey, product);
                        seckillProductBloomFilter.add(id);
                        log.info("秒杀商品已缓存（永不过期），id: {}", id);
                    } else {
                        log.warn("秒杀商品不存在，id: {}", id);
                    }
                    
                    return convertProductToResponse(product);
                    
                } finally {
                    lock.unlock();
                }
            } else {
                log.info("未获取到分布式锁，等待重试，id: {}", id);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return getSeckillProduct(id);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("查询秒杀商品被中断，id: {}", id);
            throw new RuntimeException("查询被中断", e);
        }
    }

    @Override
    public boolean purchaseSeckillProduct(Long seckillProductId, Long userId) {
        String stockKey = "seckill:stock:" + seckillProductId;
        String userKey = "seckill:user:" + seckillProductId + ":" + userId;

        // Lua 脚本：原子性检查并扣减库存
        // 返回值说明：-1=重复购买，0=库存不足，1=购买成功
        String luaScript = 
            "local userKey = KEYS[1] " +
            "local stockKey = KEYS[2] " +
            "local userId = ARGV[1] " +
            " " +
            "-- 1. 检查是否重复购买 " +
            "if redis.call('EXISTS', userKey) == 1 then " +
            "    return -1 " +
            "end " +
            " " +
            "-- 2. 检查库存 " +
            "local stock = tonumber(redis.call('GET', stockKey)) " +
            "if stock == nil or stock <= 0 then " +
            "    return 0 " +
            "end " +
            " " +
            "-- 3. 扣减库存 " +
            "redis.call('DECR', stockKey) " +
            " " +
            "-- 4. 标记用户已购买 " +
            "redis.call('SET', userKey, userId, 'EX', 86400) " +
            " " +
            "return 1";

        try {
            // 执行 Lua 脚本
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
            Long result = redisTemplate.execute(
                redisScript,
                Arrays.asList(userKey, stockKey),
                userId.toString()
            );

            // 根据返回值判断结果
            if (result == -1) {
                log.warn("用户重复购买秒杀商品，userId: {}, seckillProductId: {}", userId, seckillProductId);
                throw new RuntimeException("每人限购一件");
            }
            
            if (result == 0) {
                log.warn("秒杀商品库存不足，seckillProductId: {}, userId: {}", seckillProductId, userId);
                throw new RuntimeException("库存不足");
            }
            
            if (result == 1) {
                // 购买成功，发送 Kafka 消息
                Map<String, Object> orderMessage = new HashMap<>();
                orderMessage.put("seckillProductId", seckillProductId);
                orderMessage.put("userId", userId);
                orderMessage.put("timestamp", LocalDateTime.now().toString());
                
                String messageJson;
                try {
                    messageJson = objectMapper.writeValueAsString(orderMessage);
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    log.error("序列化秒杀订单消息失败，seckillProductId: {}, userId: {}, error: {}", 
                        seckillProductId, userId, e.getMessage(), e);
                    throw new RuntimeException("序列化消息失败", e);
                }
                
                kafkaTemplate.send("seckill-order", messageJson);
                log.info("秒杀订单消息已发送到 Kafka，seckillProductId: {}, userId: {}", seckillProductId, userId);
                
                return true;
            }
            
            // 不应该到达这里
            log.error("未知的 Lua 脚本返回值：{}, seckillProductId: {}, userId: {}", result, seckillProductId, userId);
            throw new RuntimeException("秒杀购买失败");
            
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
        
        String stockKey = "seckill:stock:" + product.getId();
        redisTemplate.opsForValue().set(stockKey, product.getStock());
        log.info("秒杀商品 Redis 库存已初始化，id: {}, stock: {}", product.getId(), product.getStock());
        
        String cacheKey = SECKILL_PRODUCT_CACHE_KEY + product.getId();
        redisTemplate.opsForValue().set(cacheKey, product);
        seckillProductBloomFilter.add(product.getId());
        log.info("秒杀商品已缓存到 Redis 和布隆过滤器，id: {}", product.getId());
        
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