package com.shopping.product.performance;

import com.shopping.product.entity.Product;
import com.shopping.product.entity.ProductImage;
import com.shopping.product.mapper.ProductMapper;
import com.shopping.product.mapper.ProductImageMapper;
import com.shopping.product.service.ProductImageService;
import com.shopping.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 商品服务性能测试
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("商品服务性能测试")
class ProductServicePerformanceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Long productId;

    @BeforeEach
    void setUp() {
        productMapper.delete(null);
        productImageMapper.delete(null);

        Product product = new Product();
        product.setName("Performance Test Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(10000);
        product.setStatus(1);
        product.setStoreId(1L);
        product.setCategoryId(1L);
        productMapper.insert(product);
        productId = product.getId();
    }

    @Test
    @DisplayName("商品查询性能测试 - 缓存命中场景")
    void testProductQuery_CacheHit() throws Exception {
        String cacheKey = "product:" + productId;
        Product product = productMapper.selectById(productId);
        redisTemplate.opsForValue().set(cacheKey, product);

        int requestCount = 500;
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(requestCount);
        AtomicInteger successCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {
            executor.submit(() -> {
                try {
                    productService.getProductById(productId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        executor.shutdown();

        System.out.println("=== 商品查询性能测试（缓存命中）===");
        System.out.println("请求数：" + requestCount);
        System.out.println("成功数：" + successCount.get());
        System.out.println("耗时：" + (endTime - startTime) + "ms");
        System.out.println("QPS: " + (requestCount * 1000.0 / (endTime - startTime)));

        assertEquals(requestCount, successCount.get());
    }

    @Test
    @DisplayName("商品图片并发添加测试")
    void testConcurrentAddProductImages() throws Exception {
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    ProductImage image = new ProductImage();
                    image.setProductId(productId);
                    image.setImageUrl("http://example.com/image" + index + ".jpg");
                    image.setSort(index);
                    image.setIsMain(index == 0 ? 1 : 0);
                    productImageMapper.insert(image);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        executor.shutdown();

        System.out.println("=== 商品图片并发添加测试 ===");
        System.out.println("线程数：" + threadCount);
        System.out.println("成功数：" + successCount.get());
        System.out.println("耗时：" + (endTime - startTime) + "ms");

        assertEquals(threadCount, successCount.get());
    }

    @Test
    @DisplayName("商品库存并发更新测试")
    void testConcurrentUpdateStock() throws Exception {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    productService.updateStock(productId, 1);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        executor.shutdown();

        Product updatedProduct = productMapper.selectById(productId);

        System.out.println("=== 商品库存并发更新测试 ===");
        System.out.println("线程数：" + threadCount);
        System.out.println("成功数：" + successCount.get());
        System.out.println("剩余库存：" + (updatedProduct != null ? updatedProduct.getStock() : 0));
        System.out.println("耗时：" + (endTime - startTime) + "ms");

        assertTrue(successCount.get() > 0);
    }
}
