package com.shopping.product.service;

import com.shopping.product.dto.request.SeckillProductRequest;
import com.shopping.product.dto.response.SeckillActivityResponse;
import com.shopping.product.dto.response.SeckillProductResponse;
import java.util.List;

public interface SeckillService {

    List<SeckillActivityResponse> getActiveActivities();

    List<SeckillProductResponse> getSeckillProductsByActivity(Long activityId);

    SeckillProductResponse getSeckillProduct(Long id);

    boolean purchaseSeckillProduct(Long seckillProductId, Long userId);

    SeckillProductResponse createSeckillProduct(SeckillProductRequest request);

    boolean initSeckillStock();
}