package com.shopping.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_images")
public class ProductImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private String imageUrl;

    private Integer sort;

    private Integer isMain;

    private LocalDateTime createTime;
}