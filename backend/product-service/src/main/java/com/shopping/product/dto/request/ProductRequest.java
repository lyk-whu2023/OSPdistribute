package com.shopping.product.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {

    private Long storeId;

    private Long categoryId;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private Integer isSeckill;
}