package com.shopping.product.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SeckillActivityResponse {

    private Long id;

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;
}