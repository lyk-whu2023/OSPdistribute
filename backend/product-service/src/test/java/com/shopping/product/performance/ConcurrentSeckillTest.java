package com.shopping.product.performance;

import com.shopping.product.entity.SeckillActivity;
import com.shopping.product.entity.SeckillProduct;
import com.shopping.product.mapper.SeckillActivityMapper;
import com.shopping.product.mapper.SeckillProductMapper;
import com.shopping.product.service.SeckillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 高并发性能测试 - 秒杀场景
 * 测试 Redis + Kafka 异步处理方案的性能和正确性
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("高并发性能测试")
class ConcurrentSeckillTest {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private SeckillProductMapper seckillProductMapper;

    @Autowired
    private SeckillActivityMapper activityMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Long seckillProductId;
    private Long activityId;

    @BeforeEach
    void setUp() {
        seckillProductMapper.delete(null);
        activityMapper.delete(null);

        SeckillActivity activity = new SeckillActivity();
        activity.setName("Performance Test");
        activity.setStartTime(LocalDateTime.now().minusHours(1));
        activity.setEndTime(LocalDateTime.now().plusHours(1));
        activity.setStatus(1);
        activityMapper.insert(activity);
        activityId = activity.getId();

        SeckillProduct product = new SeckillProduct();
        product.setActivityId(activityId);
        product.setProductId(1L);
        product.setSeckillPrice(new BigDecimal("0.01"));
        product.setStock(1000);
        product.setSold(0);
        product.setStatus(1);
        seckillProductMapper.insert(product);
        seckillProductId = product.getId();

        String stockKey = "seckill:stock:" + seckillProductId;
        redisTemplate.opsForValue().set(stockKey, 1000);
    }

    @Test
    @DisplayName("并发测试 - 100 线程同时购买")
    void testConcurrentPurchase_100Threads() throws Exception {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executor.submit(() -> {
                try {
                    boolean result = seckillService.purchaseSeckillProduct(seckillProductId, (long) userId);
                    if (result) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        executor.shutdown();

        System.out.println("总线程数：" + threadCount);
        System.out.println("成功数：" + successCount.get());
        System.out.println("失败数：" + failCount.get());
        System.out.println("耗时：" + (endTime - startTime) + "ms");
        System.out.println("QPS: " + (threadCount * 1000.0 / (endTime - startTime)));

        assertTrue(successCount.get() > 0, "应该有成功的购买请求");
        assertTrue(successCount.get() <= 1000, "成功数不能超过库存");
    }

    @Test
    @DisplayName("并发测试 - 500 线程高并发购买")
    void testConcurrentPurchase_500Threads() throws Exception {
        int threadCount = 500;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executor.submit(() -> {
                try {
                    boolean result = seckillService.purchaseSeckillProduct(seckillProductId, (long) userId);
                    if (result) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        executor.shutdown();

        System.out.println("=== 500 线程并发测试结果 ===");
        System.out.println("总线程数：" + threadCount);
        System.out.println("成功数：" + successCount.get());
        System.out.println("失败数：" + failCount.get());
        System.out.println("耗时：" + (endTime - startTime) + "ms");
        System.out.println("QPS: " + (threadCount * 1000.0 / (endTime - startTime)));

        assertTrue(successCount.get() > 0);
        assertTrue(successCount.get() <= 1000);
    }

    @Test
    @DisplayName("并发测试 - 防止超卖")
    void testNoOverselling() throws Exception {
        int threadCount = 1500;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int userId = 10000 + i;
            executor.submit(() -> {
                try {
                    if (seckillService.purchaseSeckillProduct(seckillProductId, (long) userId)) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println("=== 防超卖测试结果 ===");
        System.out.println("库存：1000, 请求数：1500, 成功数：" + successCount.get());

        assertTrue(successCount.get() <= 1000, "成功购买数不能超过库存 1000");
        assertEquals(1000, successCount.get(), "应该正好卖出 1000 件商品");
    }

    @Test
    @DisplayName("并发测试 - 防止重复购买")
    void testNoDuplicatePurchase() throws Exception {
        Long userId = 999L;
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(50);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < 50; i++) {
            executor.submit(() -> {
                try {
                    if (seckillService.purchaseSeckillProduct(seckillProductId, userId)) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println("=== 防重复购买测试结果 ===");
        System.out.println("同一用户 50 次请求，成功数：" + successCount.get());

        assertEquals(1, successCount.get(), "同一用户只能购买成功一次");
    }

    @Test
    @DisplayName("压力测试 - 响应时间测试")
    void testResponseTime() throws Exception {
        int requestCount = 200;
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(requestCount);
        List<Long> responseTimes = new CopyOnWriteArrayList<>();

        for (int i = 0; i < requestCount; i++) {
            final int userId = 20000 + i;
            executor.submit(() -> {
                long start = System.currentTimeMillis();
                try {
                    seckillService.purchaseSeckillProduct(seckillProductId, (long) userId);
                } catch (Exception e) {
                } finally {
                    long end = System.currentTimeMillis();
                    responseTimes.add(end - start);
                    latch.countDown();
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();

        long avgTime = (long) responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        long maxTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        long minTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);

        System.out.println("=== 响应时间测试结果 ===");
        System.out.println("平均响应时间：" + avgTime + "ms");
        System.out.println("最大响应时间：" + maxTime + "ms");
        System.out.println("最小响应时间：" + minTime + "ms");
        System.out.println("TP90: " + getPercentile(responseTimes, 90) + "ms");
        System.out.println("TP99: " + getPercentile(responseTimes, 99) + "ms");

        assertTrue(avgTime < 100, "平均响应时间应该小于 100ms");
    }

    private long getPercentile(List<Long> data, double percentile) {
        List<Long> sorted = new ArrayList<>(data);
        sorted.sort(Long::compare);
        int index = (int) (percentile / 100.0 * sorted.size());
        return sorted.get(Math.min(index, sorted.size() - 1));
    }
}
