package com.shopping.order.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class OrderDetailResponse {
    
    private OrderResponse order;
    
    private List<OrderItemResponse> items;
}
