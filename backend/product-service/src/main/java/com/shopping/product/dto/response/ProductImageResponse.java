package com.shopping.product.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductImageResponse {

    private Long id;

    private Long productId;

    private String imageUrl;

    private Integer sort;

    private Integer isMain;

    private LocalDateTime createTime;
}
