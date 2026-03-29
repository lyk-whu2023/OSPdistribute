package com.shopping.order.controller;

import com.shopping.order.entity.SeckillOrder;
import com.shopping.order.service.SeckillOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/seckill-orders")
@RequiredArgsConstructor
public class SeckillOrderController {
    
    private final SeckillOrderService seckillOrderService;
    
    @PostMapping
    public Mono<ResponseEntity<SeckillOrder>> createSeckillOrder(
            @RequestParam Long seckillProductId,
            @RequestParam Long userId,
            @RequestParam Long addressId) {
        SeckillOrder order = seckillOrderService.createSeckillOrder(
            seckillProductId,
            userId,
            addressId
        );
        return Mono.just(ResponseEntity.ok(order));
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SeckillOrder>> getSeckillOrderById(@PathVariable Long id) {
        return Mono.just(ResponseEntity.ok(seckillOrderService.getSeckillOrderById(id)));
    }
    
    @PutMapping("/{id}/status")
    public Mono<ResponseEntity<Void>> updateSeckillOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        seckillOrderService.updateSeckillOrderStatus(id, status);
        return Mono.just(ResponseEntity.ok().build());
    }
}
