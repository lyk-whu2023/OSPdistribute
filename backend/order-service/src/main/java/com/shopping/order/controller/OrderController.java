package com.shopping.order.controller;

import com.shopping.order.dto.request.CreateOrderRequest;
import com.shopping.order.dto.request.UpdateOrderStatusRequest;
import com.shopping.order.dto.response.OrderDetailResponse;
import com.shopping.order.dto.response.OrderResponse;
import com.shopping.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public Mono<ResponseEntity<OrderResponse>> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(
            request.getUserId(),
            request.getAddressId(),
            request.getStoreId(),
            request.getItems()
        );
        return Mono.just(ResponseEntity.ok(response));
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<OrderResponse>> getOrderById(@PathVariable Long id) {
        return Mono.just(ResponseEntity.ok(orderService.getOrderById(id)));
    }
    
    @GetMapping("/{id}/detail")
    public Mono<ResponseEntity<OrderDetailResponse>> getOrderDetailById(@PathVariable Long id) {
        return Mono.just(ResponseEntity.ok(orderService.getOrderDetailById(id)));
    }
    
    @GetMapping("/user/{userId}")
    public Mono<ResponseEntity<List<OrderResponse>>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Mono.just(ResponseEntity.ok(orderService.getUserOrders(userId, page, size)));
    }
    
    @PutMapping("/{id}/status")
    public Mono<ResponseEntity<Void>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {
        orderService.updateOrderStatus(id, request.getStatus());
        return Mono.just(ResponseEntity.ok().build());
    }
    
    @PostMapping("/{id}/cancel")
    public Mono<ResponseEntity<Void>> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Mono.just(ResponseEntity.ok().build());
    }
}
