package com.shopping.order.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    
    private Long userId;
    
    private Long addressId;
    
    private Long storeId;
    
    private List<CartItemRequest> items;
}
