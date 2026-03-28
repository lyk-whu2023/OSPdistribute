package com.shopping.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.product.dto.request.ProductRequest;
import com.shopping.product.entity.Product;
import com.shopping.product.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        productMapper.delete(null);
    }

    @Test
    void testGetProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").exists())
                .andExpect(jsonPath("$.records").isArray());
    }

    @Test
    void testGetProductById() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(100);
        product.setStatus(1);
        product.setStoreId(1L);
        product.setCategoryId(1L);
        productMapper.insert(product);

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void testCreateProduct() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("New Product");
        request.setPrice(new BigDecimal("199.99"));
        request.setStock(50);
        request.setStoreId(1L);
        request.setCategoryId(1L);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.price").value(199.99));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product product = new Product();
        product.setName("Old Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(100);
        product.setStatus(1);
        product.setStoreId(1L);
        product.setCategoryId(1L);
        productMapper.insert(product);

        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");
        request.setPrice(new BigDecimal("299.99"));

        mockMvc.perform(put("/api/products/{id}", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Product updated = productMapper.selectById(product.getId());
        assert updated != null;
        assert updated.getName().equals("Updated Product");
    }

    @Test
    void testDeleteProduct() throws Exception {
        Product product = new Product();
        product.setName("To Delete");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(100);
        product.setStatus(1);
        product.setStoreId(1L);
        product.setCategoryId(1L);
        productMapper.insert(product);

        mockMvc.perform(delete("/api/products/{id}", product.getId()))
                .andExpect(status().isOk());

        Product deleted = productMapper.selectById(product.getId());
        assert deleted == null;
    }
}
