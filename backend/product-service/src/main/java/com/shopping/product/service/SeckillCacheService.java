package com.shopping.product.service;

import com.shopping.product.dto.response.SeckillProductResponse;
import java.util.List;

public interface SeckillCacheService {
    
    void preloadAllSeckillProducts();
    
    void preloadSeckillProduct(Long productId);
    
    void evictSeckillProduct(Long productId);
    
    void evictAllSeckillProducts();
    
    void rebuildBloomFilter();
    
    long getBloomFilterCount();
    
    long getCacheSize();
    
    List<Long> getCachedProductIds();
}
