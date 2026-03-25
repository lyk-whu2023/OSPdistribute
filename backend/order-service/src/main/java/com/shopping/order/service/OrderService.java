package com.shopping.order.service;

import com.shopping.order.dto.request.CartItemRequest;
import com.shopping.order.dto.response.OrderDetailResponse;
import com.shopping.order.dto.response.OrderResponse;
import java.util.List;

public interface OrderService {
    
    OrderResponse createOrder(Long userId, Long addressId, Long storeId, List<CartItemRequest> items);
    
    OrderResponse getOrderById(Long id);
    
    OrderDetailResponse getOrderDetailById(Long id);
    
    List<OrderResponse> getUserOrders(Long userId, int page, int size);
    
    void updateOrderStatus(Long id, String status);
    
    void cancelOrder(Long id);
}
