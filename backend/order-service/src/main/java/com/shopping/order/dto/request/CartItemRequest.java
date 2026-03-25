package com.shopping.order.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemRequest {
    
    private Long productId;
    
    private Long skuId;
    
    private String productName;
    
    private String skuName;
    
    private Integer quantity;
    
    private BigDecimal price;
}
