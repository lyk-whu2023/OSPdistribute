package com.shopping.order.controller;

import com.shopping.order.dto.request.CartItemRequest;
import com.shopping.order.dto.response.CartItemResponse;
import com.shopping.order.dto.response.CartResponse;
import com.shopping.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    @GetMapping("/{userId}")
    public Mono<ResponseEntity<CartResponse>> getOrCreateCart(@PathVariable Long userId) {
        return Mono.just(ResponseEntity.ok(cartService.getOrCreateCart(userId)));
    }
    
    @GetMapping("/{userId}/items")
    public Mono<ResponseEntity<List<CartItemResponse>>> getCartItems(@PathVariable Long userId) {
        return Mono.just(ResponseEntity.ok(cartService.getCartItems(userId)));
    }
    
    @PostMapping("/{userId}/items")
    public Mono<ResponseEntity<Void>> addToCart(
            @PathVariable Long userId,
            @RequestBody CartItemRequest request) {
        cartService.addToCart(
            userId,
            request.getProductId(),
            request.getSkuId(),
            request.getQuantity(),
            request.getProductName(),
            request.getSkuName(),
            request.getPrice()
        );
        return Mono.just(ResponseEntity.ok().build());
    }
    
    @PutMapping("/items/{itemId}")
    public Mono<ResponseEntity<Void>> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody CartItemRequest request) {
        cartService.updateCartItem(itemId, request.getQuantity());
        return Mono.just(ResponseEntity.ok().build());
    }
    
    @DeleteMapping("/items/{itemId}")
    public Mono<ResponseEntity<Void>> removeCartItem(@PathVariable Long itemId) {
        cartService.removeCartItem(itemId);
        return Mono.just(ResponseEntity.ok().build());
    }
    
    @DeleteMapping("/{userId}")
    public Mono<ResponseEntity<Void>> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return Mono.just(ResponseEntity.ok().build());
    }
}
