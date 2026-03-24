package com.shopping.product.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SeckillProductResponse {

    private Long id;

    private Long activityId;

    private String activityName;

    private Long productId;

    private String productName;

    private BigDecimal seckillPrice;

    private Integer stock;

    private Integer sold;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}