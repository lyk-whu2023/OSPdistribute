package com.shopping.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.order.dto.request.CartItemRequest;
import com.shopping.order.dto.response.OrderDetailResponse;
import com.shopping.order.dto.response.OrderItemResponse;
import com.shopping.order.dto.response.OrderResponse;
import com.shopping.order.entity.Cart;
import com.shopping.order.entity.Order;
import com.shopping.order.entity.OrderItem;
import com.shopping.order.feign.ProductServiceClient;
import com.shopping.order.mapper.CartMapper;
import com.shopping.order.mapper.CartItemMapper;
import com.shopping.order.mapper.OrderItemMapper;
import com.shopping.order.mapper.OrderMapper;
import com.shopping.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductServiceClient productServiceClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, Long addressId, Long storeId, List<CartItemRequest> items) {
        String orderNo = generateOrderNo();
        
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setStoreId(storeId);
        order.setStatus("pending");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (CartItemRequest itemRequest : items) {
            totalAmount = totalAmount.add(itemRequest.getPrice().multiply(new BigDecimal(itemRequest.getQuantity())));
        }
        
        order.setTotalAmount(totalAmount);
        order.setActualAmount(totalAmount);
        orderMapper.insert(order);
        
        for (CartItemRequest itemRequest : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(itemRequest.getProductId());
            orderItem.setSkuId(itemRequest.getSkuId());
            orderItem.setProductName(itemRequest.getProductName());
            orderItem.setPrice(itemRequest.getPrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setCreateTime(LocalDateTime.now());
            
            orderItemMapper.insert(orderItem);
            
            productServiceClient.updateStock(itemRequest.getProductId(), itemRequest.getQuantity());
        }
        
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
            .eq(Cart::getUserId, userId));
        if (cart != null) {
            cartItemMapper.delete(new LambdaQueryWrapper<com.shopping.order.entity.CartItem>()
                .eq(com.shopping.order.entity.CartItem::getCartId, cart.getId()));
        }
        
        kafkaTemplate.send("order-created", order.getId().toString());
        
        return convertToResponse(order);
    }
    
    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return convertToResponse(order);
    }
    
    @Override
    public OrderDetailResponse getOrderDetailById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        OrderDetailResponse response = new OrderDetailResponse();
        response.setOrder(convertToResponse(order));
        
        List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
            .eq(OrderItem::getOrderId, id));
        
        List<OrderItemResponse> itemResponses = orderItems.stream()
            .map(this::convertToItemResponse)
            .collect(Collectors.toList());
        response.setItems(itemResponses);
        
        return response;
    }
    
    @Override
    public List<OrderResponse> getUserOrders(Long userId, int page, int size) {
        Page<Order> orderPage = new Page<>(page, size);
        Page<Order> result = orderMapper.selectPage(orderPage, new LambdaQueryWrapper<Order>()
            .eq(Order::getUserId, userId)
            .orderByDesc(Order::getCreateTime));
        
        return result.getRecords().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void updateOrderStatus(Long id, String status) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        
        if ("paid".equals(status)) {
            order.setPaymentTime(LocalDateTime.now());
        } else if ("shipping".equals(status)) {
            order.setShippingTime(LocalDateTime.now());
        } else if ("completed".equals(status)) {
            order.setCompleteTime(LocalDateTime.now());
        }
        
        orderMapper.updateById(order);
        
        kafkaTemplate.send("order-status-updated", id + ":" + status);
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("只能取消待支付订单");
        }
        
        order.setStatus("cancelled");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
            .eq(OrderItem::getOrderId, id));
        
        for (OrderItem item : orderItems) {
            productServiceClient.updateStock(item.getProductId(), -item.getQuantity());
        }
        
        kafkaTemplate.send("order-cancelled", id.toString());
    }
    
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + 
               UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
    
    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setUserId(order.getUserId());
        response.setAddressId(order.getAddressId());
        response.setStoreId(order.getStoreId());
        response.setTotalAmount(order.getTotalAmount());
        response.setActualAmount(order.getActualAmount());
        response.setStatus(order.getStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setPaymentTime(order.getPaymentTime());
        response.setShippingTime(order.getShippingTime());
        response.setCompleteTime(order.getCompleteTime());
        response.setCreateTime(order.getCreateTime());
        response.setUpdateTime(order.getUpdateTime());
        return response;
    }
    
    private OrderItemResponse convertToItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setSkuId(item.getSkuId());
        response.setProductName(item.getProductName());
        response.setSkuName(item.getSkuName());
        response.setPrice(item.getPrice());
        response.setQuantity(item.getQuantity());
        response.setSubtotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        return response;
    }
}
