package com.shopping.product.controller;

import com.shopping.product.dto.request.SeckillProductRequest;
import com.shopping.product.dto.response.SeckillActivityResponse;
import com.shopping.product.dto.response.SeckillProductResponse;
import com.shopping.product.service.SeckillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

    @PostMapping("/admin/init-stock")
    public ResponseEntity<Boolean> initSeckillStock() {
        log.info("接收到初始化秒杀库存请求");
        boolean result = seckillService.initSeckillStock();
        log.info("初始化秒杀库存结果：{}", result);
        return ResponseEntity.ok(result);
    }
}