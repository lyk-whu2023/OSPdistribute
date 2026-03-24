package com.shopping.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product_skus")
public class ProductSku {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private String skuName;

    private BigDecimal price;

    private Integer stock;

    private Integer sales;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}