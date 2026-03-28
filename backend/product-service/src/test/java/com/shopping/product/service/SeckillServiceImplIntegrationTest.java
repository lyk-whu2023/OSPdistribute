package com.shopping.product.service;

import com.shopping.product.ProductServiceApplication;
import com.shopping.product.dto.request.SeckillProductRequest;
import com.shopping.product.dto.response.SeckillProductResponse;
import com.shopping.product.entity.SeckillProduct;
import com.shopping.product.mapper.SeckillProductMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {SeckillServiceImplIntegrationTest.TestApplication.class, com.shopping.product.config.TestRedisConfig.class})
    @ActiveProfiles("test")
    @Transactional
    class SeckillServiceImplIntegrationTest {

        @SpringBootApplication(exclude = {
            org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration.class
        })
        @MapperScan("com.shopping.product.mapper")
        static class TestApplication {
        }

    @Autowired
    private SeckillProductMapper seckillProductMapper;

    @Autowired
    private SeckillServiceImpl seckillService;

    @Test
    void testCreateSeckillProduct() {
        SeckillProductRequest request = new SeckillProductRequest();
        request.setActivityId(1L);
        request.setProductId(2L);
        request.setSeckillPrice(new BigDecimal("9.99"));
        request.setStock(100);

        SeckillProductResponse response = seckillService.createSeckillProduct(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(new BigDecimal("9.99"), response.getSeckillPrice());
        assertEquals(100, response.getStock());
    }
}
