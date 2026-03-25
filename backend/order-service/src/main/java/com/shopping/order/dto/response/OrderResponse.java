package com.shopping.order.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponse {
    
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private Long addressId;
    
    private String addressDetail;
    
    private Long storeId;
    
    private String storeName;
    
    private BigDecimal totalAmount;
    
    private BigDecimal actualAmount;
    
    private String status;
    
    private String paymentMethod;
    
    private LocalDateTime paymentTime;
    
    private LocalDateTime shippingTime;
    
    private LocalDateTime completeTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
