package com.shopping.order.controller;

import com.shopping.order.dto.request.CreateOrderRequest;
import com.shopping.order.dto.request.UpdateOrderStatusRequest;
import com.shopping.order.dto.response.OrderDetailResponse;
import com.shopping.order.dto.response.OrderResponse;
import com.shopping.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        System.out.println("=== 收到订单创建请求 ===");
        System.out.println("userId: " + request.getUserId());
        System.out.println("addressId: " + request.getAddressId());
        System.out.println("storeId: " + request.getStoreId());
        System.out.println("items 数量：" + request.getItems().size());
        for (var item : request.getItems()) {
            System.out.println("  - productId: " + item.getProductId() + ", productName: " + item.getProductName() + ", price: " + item.getPrice() + ", quantity: " + item.getQuantity());
        }
        
        OrderResponse response = orderService.createOrder(
            request.getUserId(),
            request.getAddressId(),
            request.getStoreId(),
            request.getItems()
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    
    @GetMapping("/{id}/detail")
    public ResponseEntity<OrderDetailResponse> getOrderDetailById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDetailById(id));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getUserOrders(userId, page, size));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {
        orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }
}
