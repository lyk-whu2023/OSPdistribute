package com.shopping.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.product.dto.response.SeckillProductResponse;
import com.shopping.product.entity.SeckillProduct;
import com.shopping.product.mapper.SeckillProductMapper;
import com.shopping.product.service.SeckillCacheService;
import com.shopping.product.service.SeckillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillCacheServiceImpl implements SeckillCacheService {

    private final SeckillProductMapper seckillProductMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;
    private final RBloomFilter<Long> seckillProductBloomFilter;
    private final SeckillService seckillService;

    private static final String SECKILL_PRODUCT_CACHE_KEY = "seckill:product:";
    private static final String SECKILL_LOCK_KEY = "seckill:lock:product:";
    private static final String BLOOM_FILTER_KEY = "seckill:product:bloom";
    private static final long LOCK_WAIT_TIME = 3;
    private static final long LOCK_LEASE_TIME = 10;

    @Override
    public void preloadAllSeckillProducts() {
        log.info("开始预加载所有秒杀商品到缓存");
        
        List<SeckillProduct> products = seckillProductMapper.selectList(
            new LambdaQueryWrapper<SeckillProduct>().eq(SeckillProduct::getStatus, 1)
        );
        
        int successCount = 0;
        int failCount = 0;
        
        for (SeckillProduct product : products) {
            try {
                preloadSeckillProduct(product.getId());
                successCount++;
            } catch (Exception e) {
                log.error("预加载秒杀商品失败，productId: {}, error: {}", product.getId(), e.getMessage());
                failCount++;
            }
        }
        
        log.info("秒杀商品预加载完成，总数: {}, 成功: {}, 失败: {}", products.size(), successCount, failCount);
    }

    @Override
    public void preloadSeckillProduct(Long productId) {
        log.info("预加载秒杀商品，productId: {}", productId);
        
        String cacheKey = SECKILL_PRODUCT_CACHE_KEY + productId;
        String lockKey = SECKILL_LOCK_KEY + productId;
        
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            boolean acquired = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            
            if (acquired) {
                try {
                    Object cached = redisTemplate.opsForValue().get(cacheKey);
                    if (cached != null) {
                        log.info("秒杀商品已存在缓存中，无需预加载，productId: {}", productId);
                        return;
                    }
                    
                    SeckillProduct product = seckillProductMapper.selectById(productId);
                    if (product != null) {
                        redisTemplate.opsForValue().set(cacheKey, product);
                        seckillProductBloomFilter.add(productId);
                        log.info("秒杀商品预加载成功，productId: {}", productId);
                    } else {
                        log.warn("秒杀商品不存在，productId: {}", productId);
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("获取分布式锁失败，可能正在预加载，productId: {}", productId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("预加载秒杀商品被中断，productId: {}", productId);
        } catch (Exception e) {
            log.error("预加载秒杀商品异常，productId: {}, error: {}", productId, e.getMessage(), e);
        }
    }

    @Override
    public void evictSeckillProduct(Long productId) {
        log.info("手动下线秒杀商品，productId: {}", productId);
        
        String cacheKey = SECKILL_PRODUCT_CACHE_KEY + productId;
        String lockKey = SECKILL_LOCK_KEY + productId;
        
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            boolean acquired = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            
            if (acquired) {
                try {
                    redisTemplate.delete(cacheKey);
                    log.info("秒杀商品已从缓存中删除，productId: {}", productId);
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("获取分布式锁失败，无法下线商品，productId: {}", productId);
                throw new RuntimeException("获取分布式锁失败，请稍后重试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("下线秒杀商品被中断，productId: {}", productId);
        } catch (Exception e) {
            log.error("下线秒杀商品异常，productId: {}, error: {}", productId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void evictAllSeckillProducts() {
        log.info("开始手动下线所有秒杀商品");
        
        Set<String> keys = redisTemplate.keys(SECKILL_PRODUCT_CACHE_KEY + "*");
        
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("所有秒杀商品已从缓存中删除，数量: {}", keys.size());
        } else {
            log.info("缓存中没有秒杀商品");
        }
    }

    @Override
    public void rebuildBloomFilter() {
        log.info("开始重建布隆过滤器");
        
        seckillProductBloomFilter.delete();
        seckillProductBloomFilter.tryInit(100000L, 0.01);
        
        List<SeckillProduct> products = seckillProductMapper.selectList(
            new LambdaQueryWrapper<SeckillProduct>().eq(SeckillProduct::getStatus, 1)
        );
        
        int count = 0;
        for (SeckillProduct product : products) {
            seckillProductBloomFilter.add(product.getId());
            count++;
        }
        
        log.info("布隆过滤器重建完成，加载商品数量: {}", count);
    }

    @Override
    public long getBloomFilterCount() {
        return seckillProductBloomFilter.count();
    }

    @Override
    public long getCacheSize() {
        Set<String> keys = redisTemplate.keys(SECKILL_PRODUCT_CACHE_KEY + "*");
        return keys != null ? keys.size() : 0;
    }

    @Override
    public List<Long> getCachedProductIds() {
        Set<String> keys = redisTemplate.keys(SECKILL_PRODUCT_CACHE_KEY + "*");
        
        if (keys == null || keys.isEmpty()) {
            return new ArrayList<>();
        }
        
        return keys.stream()
            .map(key -> {
                String idStr = key.replace(SECKILL_PRODUCT_CACHE_KEY, "");
                try {
                    return Long.parseLong(idStr);
                } catch (NumberFormatException e) {
                    return null;
                }
            })
            .filter(id -> id != null)
            .collect(Collectors.toList());
    }
}
