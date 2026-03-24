package com.shopping.product.service;

import com.shopping.product.dto.request.ProductImageRequest;
import com.shopping.product.dto.response.ProductImageResponse;
import java.util.List;

public interface ProductImageService {

    List<ProductImageResponse> getProductImages(Long productId);

    ProductImageResponse getProductImage(Long id);

    ProductImageResponse addProductImage(ProductImageRequest request);

    void updateProductImage(Long id, ProductImageRequest request);

    void deleteProductImage(Long id);

    void setMainImage(Long productId, Long imageId);
}
