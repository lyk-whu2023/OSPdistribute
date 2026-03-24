package com.shopping.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("products")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long storeId;

    private Long categoryId;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private Integer sales;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isSeckill;
}