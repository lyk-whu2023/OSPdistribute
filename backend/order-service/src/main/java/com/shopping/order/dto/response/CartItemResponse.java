package com.shopping.order.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemResponse {
    
    private Long id;
    
    private Long productId;
    
    private Long skuId;
    
    private String productName;
    
    private String skuName;
    
    private Integer quantity;
    
    private BigDecimal price;
    
    private BigDecimal subtotal;
}
