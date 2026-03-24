package com.shopping.product.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryResponse {

    private Long id;

    private String name;

    private Long parentId;

    private Integer level;

    private Integer sort;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}