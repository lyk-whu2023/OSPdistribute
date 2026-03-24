package com.shopping.product.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SeckillProductRequest {

    private Long activityId;

    private Long productId;

    private BigDecimal seckillPrice;

    private Integer stock;
}