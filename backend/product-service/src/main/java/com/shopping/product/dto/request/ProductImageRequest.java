package com.shopping.product.dto.request;

import lombok.Data;

@Data
public class ProductImageRequest {

    private Long productId;

    private String imageUrl;

    private Integer sort;

    private Integer isMain;
}
