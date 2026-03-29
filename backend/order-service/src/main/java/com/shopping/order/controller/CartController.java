package com.shopping.order.controller;

import com.shopping.order.dto.request.CartItemRequest;
import com.shopping.order.dto.response.CartItemResponse;
import com.shopping.order.dto.response.CartResponse;
import com.shopping.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("=== 测试接口被调用 ===");
        return ResponseEntity.ok("Cart Controller is working!");
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getOrCreateCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userId));
    }
    
    @GetMapping("/{userId}/items")
    public ResponseEntity<List<CartItemResponse>> getCartItems(@PathVariable Long userId) {
        System.out.println("=== 收到购物车请求，userId: " + userId);
        List<CartItemResponse> items = cartService.getCartItems(userId);
        System.out.println("=== 返回购物车数据，item 数量：" + items.size());
        return ResponseEntity.ok(items);
    }
    
    @PostMapping("/{userId}/items")
    public ResponseEntity<Void> addToCart(
            @PathVariable Long userId,
            @RequestBody CartItemRequest request) {
        cartService.addToCart(
            userId,
            request.getProductId(),
            request.getSkuId(),
            request.getQuantity(),
            request.getProductName(),
            request.getPrice()
        );
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/items/{itemId}")
    public ResponseEntity<Void> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody CartItemRequest request) {
        cartService.updateCartItem(itemId, request.getQuantity());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long itemId) {
        cartService.removeCartItem(itemId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
}
