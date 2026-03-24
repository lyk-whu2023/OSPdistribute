package com.shopping.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_sku_attributes")
public class ProductSkuAttribute {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long skuId;

    private Long attributeValueId;

    private LocalDateTime createTime;
}