package com.shopping.product.dto.request;

import lombok.Data;

@Data
public class CategoryRequest {

    private String name;

    private Long parentId;

    private Integer level;

    private Integer sort;
}