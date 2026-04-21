package com.shopping.product.controller;

import com.shopping.product.service.SeckillCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/seckill/cache")
@RequiredArgsConstructor
public class SeckillCacheController {

    private final SeckillCacheService seckillCacheService;

    @PostMapping("/preload/all")
    public ResponseEntity<Map<String, Object>> preloadAll() {
        log.info("收到预加载所有秒杀商品请求");
        
        long startTime = System.currentTimeMillis();
        seckillCacheService.preloadAllSeckillProducts();
        long duration = System.currentTimeMillis() - startTime;
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "预加载完成");
        result.put("duration", duration + "ms");
        result.put("cacheSize", seckillCacheService.getCacheSize());
        result.put("bloomFilterCount", seckillCacheService.getBloomFilterCount());
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/preload/{productId}")
    public ResponseEntity<Map<String, Object>> preloadProduct(@PathVariable Long productId) {
        log.info("收到预加载秒杀商品请求，productId: {}", productId);
        
        seckillCacheService.preloadSeckillProduct(productId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "预加载完成");
        result.put("productId", productId);
        
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/evict/{productId}")
    public ResponseEntity<Map<String, Object>> evictProduct(@PathVariable Long productId) {
        log.info("收到手动下线秒杀商品请求，productId: {}", productId);
        
        try {
            seckillCacheService.evictSeckillProduct(productId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "商品已下线");
            result.put("productId", productId);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("下线秒杀商品失败，productId: {}, error: {}", productId, e.getMessage());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "下线失败: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @DeleteMapping("/evict/all")
    public ResponseEntity<Map<String, Object>> evictAll() {
        log.info("收到手动下线所有秒杀商品请求");
        
        seckillCacheService.evictAllSeckillProducts();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "所有商品已下线");
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/bloom/rebuild")
    public ResponseEntity<Map<String, Object>> rebuildBloomFilter() {
        log.info("收到重建布隆过滤器请求");
        
        long startTime = System.currentTimeMillis();
        seckillCacheService.rebuildBloomFilter();
        long duration = System.currentTimeMillis() - startTime;
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "布隆过滤器重建完成");
        result.put("count", seckillCacheService.getBloomFilterCount());
        result.put("duration", duration + "ms");
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        log.info("收到查询缓存统计请求");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheSize", seckillCacheService.getCacheSize());
        stats.put("bloomFilterCount", seckillCacheService.getBloomFilterCount());
        stats.put("cachedProductIds", seckillCacheService.getCachedProductIds());
        
        return ResponseEntity.ok(stats);
    }
}
