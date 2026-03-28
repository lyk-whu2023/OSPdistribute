package com.shopping.product.controller;

import com.shopping.product.dto.request.SeckillProductRequest;
import com.shopping.product.dto.response.SeckillActivityResponse;
import com.shopping.product.dto.response.SeckillProductResponse;
import com.shopping.product.service.SeckillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seckill")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillService seckillService;

    @GetMapping("/activities")
    public ResponseEntity<List<SeckillActivityResponse>> getActiveActivities() {
        return ResponseEntity.ok(seckillService.getActiveActivities());
    }

    @GetMapping("/activities/{activityId}/products")
    public ResponseEntity<List<SeckillProductResponse>> getSeckillProductsByActivity(
            @PathVariable Long activityId) {
        return ResponseEntity.ok(seckillService.getSeckillProductsByActivity(activityId));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<SeckillProductResponse> getSeckillProduct(@PathVariable Long id) {
        return ResponseEntity.ok(seckillService.getSeckillProduct(id));
    }

    @PostMapping("/products/{seckillProductId}/purchase")
    public ResponseEntity<Boolean> purchaseSeckillProduct(
            @PathVariable Long seckillProductId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(seckillService.purchaseSeckillProduct(seckillProductId, userId));
    }

    @PostMapping("/products")
    public ResponseEntity<SeckillProductResponse> createSeckillProduct(
            @RequestBody SeckillProductRequest request) {
        return ResponseEntity.ok(seckillService.createSeckillProduct(request));
    }
}