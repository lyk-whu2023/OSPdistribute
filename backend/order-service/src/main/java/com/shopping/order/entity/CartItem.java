package com.shopping.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cart_items")
public class CartItem {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long cartId;
    
    private Long productId;
    
    private Long skuId;
    
    private String productName;
    
    private Integer quantity;
    
    private BigDecimal price;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
