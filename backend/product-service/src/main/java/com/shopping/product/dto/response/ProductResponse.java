package com.shopping.product.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponse {

    private Long id;

    private Long storeId;

    private Long categoryId;

    private String categoryName;

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