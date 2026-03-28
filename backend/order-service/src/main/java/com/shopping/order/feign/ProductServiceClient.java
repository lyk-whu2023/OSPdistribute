package com.shopping.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    
    @PutMapping("/api/products/{productId}/stock/{quantity}")
    void updateStock(@PathVariable Long productId, @PathVariable Integer quantity);
}
