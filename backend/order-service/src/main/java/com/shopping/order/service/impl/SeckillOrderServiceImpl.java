package com.shopping.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.order.entity.SeckillOrder;
import com.shopping.order.feign.ProductServiceClient;
import com.shopping.order.mapper.SeckillOrderMapper;
import com.shopping.order.service.SeckillOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SeckillOrderServiceImpl implements SeckillOrderService {
    
    private final SeckillOrderMapper seckillOrderMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ProductServiceClient productServiceClient;
    
    @Override
    @Transactional
    public SeckillOrder createSeckillOrder(Long seckillProductId, Long userId, Long addressId) {
        String userKey = "seckill:user:" + seckillProductId + ":" + userId;
        Boolean hasPurchased = redisTemplate.opsForValue().setIfAbsent(userKey, "1", 1, TimeUnit.DAYS);
        if (hasPurchased == null || !hasPurchased) {
            throw new RuntimeException("每人限购一件");
        }
        
        String stockKey = "seckill:stock:" + seckillProductId;
        Long stock = redisTemplate.opsForValue().decrement(stockKey);
        if (stock == null || stock < 0) {
            redisTemplate.delete(userKey);
            throw new RuntimeException("库存不足");
        }
        
        SeckillOrder order = new SeckillOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setSeckillProductId(seckillProductId);
        order.setProductId(seckillProductId);
        order.setStatus("pending");
        order.setCreateTime(LocalDateTime.now());
        seckillOrderMapper.insert(order);
        
        kafkaTemplate.send("seckill-order", seckillProductId + ":" + userId);
        
        return order;
    }
    
    @Override
    public SeckillOrder getSeckillOrderById(Long id) {
        return seckillOrderMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public void updateSeckillOrderStatus(Long id, String status) {
        SeckillOrder order = seckillOrderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("秒杀订单不存在");
        }
        
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        
        if ("paid".equals(status)) {
            order.setPaymentTime(LocalDateTime.now());
        } else if ("shipping".equals(status)) {
            order.setShippingTime(LocalDateTime.now());
        } else if ("completed".equals(status)) {
            order.setCompleteTime(LocalDateTime.now());
        }
        
        seckillOrderMapper.updateById(order);
    }
    
    private String generateOrderNo() {
        return "SECKILL" + System.currentTimeMillis() + 
               UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
