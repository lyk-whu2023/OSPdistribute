package com.shopping.order.service;

import com.shopping.order.dto.response.CartItemResponse;
import com.shopping.order.dto.response.CartResponse;
import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    
    CartResponse getOrCreateCart(Long userId);
    
    List<CartItemResponse> getCartItems(Long userId);
    
    void addToCart(Long userId, Long productId, Long skuId, Integer quantity, String productName, String skuName, BigDecimal price);
    
    void updateCartItem(Long cartItemId, Integer quantity);
    
    void removeCartItem(Long cartItemId);
    
    void clearCart(Long userId);
}
