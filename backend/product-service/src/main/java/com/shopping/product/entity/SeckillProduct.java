package com.shopping.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("seckill_products")
public class SeckillProduct {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long activityId;

    private Long productId;

    private BigDecimal seckillPrice;

    private Integer stock;

    private Integer sold;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}