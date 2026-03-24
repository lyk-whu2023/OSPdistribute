package com.shopping.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.product.dto.request.ProductRequest;
import com.shopping.product.dto.response.ProductResponse;
import com.shopping.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(productService.getProducts(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{productId}/stock/{quantity}")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long productId,
            @PathVariable Integer quantity) {
        productService.updateStock(productId, quantity);
        return ResponseEntity.ok().build();
    }
}