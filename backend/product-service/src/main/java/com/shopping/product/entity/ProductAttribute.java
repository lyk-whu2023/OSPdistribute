package com.shopping.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_attributes")
public class ProductAttribute {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private String attributeName;

    private LocalDateTime createTime;
}