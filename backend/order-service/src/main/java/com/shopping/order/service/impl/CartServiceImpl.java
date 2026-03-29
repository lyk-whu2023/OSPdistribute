package com.shopping.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.order.dto.response.CartItemResponse;
import com.shopping.order.dto.response.CartResponse;
import com.shopping.order.entity.Cart;
import com.shopping.order.entity.CartItem;
import com.shopping.order.mapper.CartMapper;
import com.shopping.order.mapper.CartItemMapper;
import com.shopping.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    
    @Override
    public CartResponse getOrCreateCart(Long userId) {
        Cart cart = getCart(userId);
        
        if (cart == null) {
            cart = createCart(userId);
        }
        
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUserId());
        
        List<CartItemResponse> items = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
            .eq(CartItem::getCartId, cart.getId()))
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        response.setItems(items);
        
        BigDecimal totalAmount = items.stream()
            .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalAmount(totalAmount);
        
        return response;
    }
    
    @Override
    public List<CartItemResponse> getCartItems(Long userId) {
        Cart cart = getCart(userId);
        if (cart == null) {
            return List.of();
        }
        return cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
            .eq(CartItem::getCartId, cart.getId()))
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void addToCart(Long userId, Long productId, Long skuId, Integer quantity, String productName, BigDecimal price) {
        Cart cart = getCart(userId);
        if (cart == null) {
            cart = createCart(userId);
        }
        
        CartItem existingItem = cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
            .eq(CartItem::getCartId, cart.getId())
            .eq(CartItem::getProductId, productId)
            .eq(CartItem::getSkuId, skuId));
        
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setProductName(productName);
            existingItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.updateById(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCartId(cart.getId());
            newItem.setProductId(productId);
            newItem.setSkuId(skuId);
            newItem.setProductName(productName);
            newItem.setQuantity(quantity);
            newItem.setPrice(price);
            newItem.setCreateTime(LocalDateTime.now());
            newItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.insert(newItem);
        }
    }
    
    @Override
    @Transactional
    public void updateCartItem(Long cartItemId, Integer quantity) {
        CartItem item = cartItemMapper.selectById(cartItemId);
        if (item == null) {
            throw new RuntimeException("购物车商品不存在");
        }
        
        if (quantity <= 0) {
            removeCartItem(cartItemId);
            return;
        }
        
        item.setQuantity(quantity);
        item.setUpdateTime(LocalDateTime.now());
        cartItemMapper.updateById(item);
    }
    
    @Override
    @Transactional
    public void removeCartItem(Long cartItemId) {
        cartItemMapper.deleteById(cartItemId);
    }
    
    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCart(userId);
        if (cart != null) {
            cartItemMapper.delete(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCartId, cart.getId()));
        }
    }
    
    private Cart getCart(Long userId) {
        return cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
            .eq(Cart::getUserId, userId));
    }
    
    private Cart createCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setCreateTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());
        cartMapper.insert(cart);
        return cart;
    }
    
    private CartItemResponse convertToResponse(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setSkuId(item.getSkuId());
        response.setProductName(item.getProductName());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        response.setSubtotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        return response;
    }
}
