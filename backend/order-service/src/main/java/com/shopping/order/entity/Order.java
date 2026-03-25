package com.shopping.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private Long addressId;
    
    private Long storeId;
    
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
