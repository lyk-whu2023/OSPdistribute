package com.shopping.product.service;

import com.shopping.product.ProductServiceApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.product.dto.request.ProductRequest;
import com.shopping.product.dto.response.ProductResponse;
import com.shopping.product.entity.Product;
import com.shopping.product.mapper.ProductMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ProductServiceImplIntegrationTest.TestApplication.class, com.shopping.product.config.TestRedisConfig.class})
    @ActiveProfiles("test")
    @Transactional
    class ProductServiceImplIntegrationTest {

        @SpringBootApplication(exclude = {
            org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration.class
        })
        @MapperScan("com.shopping.product.mapper")
        static class TestApplication {
        }

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("Integration Test Product");
        request.setPrice(new BigDecimal("299.99"));
        request.setStoreId(1L);
        request.setCategoryId(1L);
        request.setStock(100);

        ProductResponse response = productService.createProduct(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Integration Test Product", response.getName());
        assertEquals(new BigDecimal("299.99"), response.getPrice());
    }

    @Test
    void testGetProductById() {
        // 先创建商品
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(new BigDecimal("199.99"));
        product.setStoreId(1L);
        product.setCategoryId(1L);
        product.setStock(50);
        product.setStatus(1);
        productMapper.insert(product);

        // 测试查询
        ProductResponse response = productService.getProductById(product.getId());

        assertNotNull(response);
        assertEquals(product.getId(), response.getId());
        assertEquals("Test Product", response.getName());
    }

    @Test
    void testUpdateProduct() {
        // 先创建商品
        Product product = new Product();
        product.setName("Old Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setStoreId(1L);
        product.setCategoryId(1L);
        product.setStock(50);
        product.setStatus(1);
        productMapper.insert(product);

        // 测试更新
        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");
        request.setPrice(new BigDecimal("399.99"));

        ProductResponse response = productService.updateProduct(product.getId(), request);

        assertNotNull(response);
        assertEquals("Updated Product", response.getName());
        assertEquals(new BigDecimal("399.99"), response.getPrice());
    }

    @Test
    void testDeleteProduct() {
        // 先创建商品
        Product product = new Product();
        product.setName("To Delete");
        product.setPrice(new BigDecimal("99.99"));
        product.setStoreId(1L);
        product.setCategoryId(1L);
        product.setStock(50);
        product.setStatus(1);
        productMapper.insert(product);

        // 测试删除
        assertDoesNotThrow(() -> productService.deleteProduct(product.getId()));

        // 验证删除
        Product deleted = productMapper.selectById(product.getId());
        assertNull(deleted);
    }

    @Test
    void testUpdateStock_Success() {
        // 先创建商品
        Product product = new Product();
        product.setName("Stock Test");
        product.setPrice(new BigDecimal("99.99"));
        product.setStoreId(1L);
        product.setCategoryId(1L);
        product.setStock(100);
        product.setSales(50);
        product.setStatus(1);
        productMapper.insert(product);

        // 测试更新库存
        assertDoesNotThrow(() -> productService.updateStock(product.getId(), 10));

        // 验证库存更新
        Product updated = productMapper.selectById(product.getId());
        assertEquals(90, updated.getStock());
        assertEquals(60, updated.getSales());
    }

    @Test
    void testUpdateStock_InsufficientStock() {
        // 先创建商品
        Product product = new Product();
        product.setName("Stock Test");
        product.setPrice(new BigDecimal("99.99"));
        product.setStoreId(1L);
        product.setCategoryId(1L);
        product.setStock(10);
        product.setStatus(1);
        productMapper.insert(product);

        // 测试库存不足
        assertThrows(RuntimeException.class, () -> productService.updateStock(product.getId(), 20));
    }
}
