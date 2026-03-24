package com.shopping.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_attribute_values")
public class ProductAttributeValue {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long attributeId;

    private String valueName;

    private LocalDateTime createTime;
}