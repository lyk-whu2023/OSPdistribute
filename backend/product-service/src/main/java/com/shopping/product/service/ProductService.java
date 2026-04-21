package com.shopping.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.product.dto.request.ProductRequest;
import com.shopping.product.dto.response.ProductResponse;

public interface ProductService {

    Page<ProductResponse> getProducts(Integer page, Integer size);

    Page<ProductResponse> getProducts(Integer page, Integer size, String keyword, Long categoryId);

    ProductResponse getProductById(Long id);

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    void updateStock(Long productId, Integer quantity);

    void updateProductStatus(Long id, Integer status);
}