package com.shopping.product.controller;

import com.shopping.product.dto.request.ProductImageRequest;
import com.shopping.product.dto.response.ProductImageResponse;
import com.shopping.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductImageResponse>> getProductImages(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getProductImages(productId));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ProductImageResponse> getProductImage(@PathVariable Long id) {
        return ResponseEntity.ok(productImageService.getProductImage(id));
    }

    @PostMapping
    public ResponseEntity<ProductImageResponse> addProductImage(@RequestBody ProductImageRequest request) {
        return ResponseEntity.ok(productImageService.addProductImage(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProductImage(@PathVariable Long id, @RequestBody ProductImageRequest request) {
        productImageService.updateProductImage(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Long id) {
        productImageService.deleteProductImage(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{productId}/main/{imageId}")
    public ResponseEntity<Void> setMainImage(@PathVariable Long productId, @PathVariable Long imageId) {
        productImageService.setMainImage(productId, imageId);
        return ResponseEntity.ok().build();
    }
}