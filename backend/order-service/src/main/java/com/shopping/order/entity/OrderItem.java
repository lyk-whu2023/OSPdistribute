package com.shopping.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_items")
public class OrderItem {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private Long productId;
    
    private Long skuId;
    
    private String productName;
    
    private String skuName;
    
    private BigDecimal price;
    
    private Integer quantity;
    
    private LocalDateTime createTime;
}
