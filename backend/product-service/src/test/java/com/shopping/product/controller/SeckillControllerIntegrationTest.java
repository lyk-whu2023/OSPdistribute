package com.shopping.product.controller;

import com.shopping.product.entity.SeckillActivity;
import com.shopping.product.entity.SeckillProduct;
import com.shopping.product.mapper.SeckillActivityMapper;
import com.shopping.product.mapper.SeckillProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SeckillControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SeckillActivityMapper activityMapper;

    @Autowired
    private SeckillProductMapper seckillProductMapper;

    @BeforeEach
    void setUp() {
        seckillProductMapper.delete(null);
        activityMapper.delete(null);
    }

    @Test
    void testGetActiveActivities() throws Exception {
        SeckillActivity activity = new SeckillActivity();
        activity.setName("Test Seckill");
        activity.setStartTime(LocalDateTime.now().minusHours(1));
        activity.setEndTime(LocalDateTime.now().plusHours(1));
        activity.setStatus(1);
        activityMapper.insert(activity);

        mockMvc.perform(get("/api/seckill/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetSeckillProductsByActivity() throws Exception {
        SeckillActivity activity = new SeckillActivity();
        activity.setName("Test Activity");
        activity.setStartTime(LocalDateTime.now().minusHours(1));
        activity.setEndTime(LocalDateTime.now().plusHours(1));
        activity.setStatus(1);
        activityMapper.insert(activity);

        SeckillProduct product = new SeckillProduct();
        product.setActivityId(activity.getId());
        product.setProductId(1L);
        product.setSeckillPrice(new BigDecimal("9.99"));
        product.setStock(100);
        product.setSold(0);
        product.setStatus(1);
        seckillProductMapper.insert(product);

        mockMvc.perform(get("/api/seckill/activities/{activityId}/products", activity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].seckillPrice").value(9.99));
    }

    @Test
    void testPurchaseSeckillProduct() throws Exception {
        SeckillActivity activity = new SeckillActivity();
        activity.setName("Flash Sale");
        activity.setStartTime(LocalDateTime.now().minusHours(1));
        activity.setEndTime(LocalDateTime.now().plusHours(1));
        activity.setStatus(1);
        activityMapper.insert(activity);

        SeckillProduct product = new SeckillProduct();
        product.setActivityId(activity.getId());
        product.setProductId(1L);
        product.setSeckillPrice(new BigDecimal("0.01"));
        product.setStock(10);
        product.setSold(0);
        product.setStatus(1);
        seckillProductMapper.insert(product);

        mockMvc.perform(post("/api/seckill/products/{id}/purchase", product.getId())
                .param("userId", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
