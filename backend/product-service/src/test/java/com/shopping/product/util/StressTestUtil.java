package com.shopping.product.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 压力测试工具类
 */
public class StressTestUtil {

    /**
     * 执行并发测试
     * @param task 测试任务
     * @param threadCount 线程数
     * @param timeout 超时时间（秒）
     * @return 测试结果统计
     */
    public static StressTestResult executeStressTest(Runnable task, int threadCount, long timeout) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    task.run();
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double qps = threadCount * 1000.0 / totalTime;

        executor.shutdown();

        return new StressTestResult(
                threadCount,
                successCount.get(),
                failCount.get(),
                totalTime,
                qps
        );
    }

    /**
     * 执行阶梯式压力测试
     * @param task 测试任务
     * @param initialThreads 初始线程数
     * @param maxThreads 最大线程数
     * @param stepIncrement 步进增量
     * @param durationPerLevel 每级持续时间（秒）
     * @return 测试结果
     */
    public static StressTestResult executeRampUpTest(Runnable task, int initialThreads, 
                                                      int maxThreads, int stepIncrement, 
                                                      int durationPerLevel) {
        StressTestResult finalResult = null;
        
        for (int threads = initialThreads; threads <= maxThreads; threads += stepIncrement) {
            System.out.println("\n=== 当前并发数：" + threads + " ===");
            StressTestResult result = executeStressTest(task, threads, durationPerLevel);
            result.printReport();
            finalResult = result;
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        return finalResult;
    }

    /**
     * 测试结果类
     */
    public static class StressTestResult {
        private final int totalThreads;
        private final int successCount;
        private final int failCount;
        private final long totalTime;
        private final double qps;

        public StressTestResult(int totalThreads, int successCount, int failCount, 
                               long totalTime, double qps) {
            this.totalThreads = totalThreads;
            this.successCount = successCount;
            this.failCount = failCount;
            this.totalTime = totalTime;
            this.qps = qps;
        }

        public void printReport() {
            System.out.println("========== 压力测试结果 ==========");
            System.out.println("总线程数：" + totalThreads);
            System.out.println("成功数：" + successCount);
            System.out.println("失败数：" + failCount);
            System.out.println("成功率：" + String.format("%.2f", successCount * 100.0 / totalThreads) + "%");
            System.out.println("总耗时：" + totalTime + "ms");
            System.out.println("QPS: " + String.format("%.2f", qps));
            System.out.println("================================");
        }

        public int getTotalThreads() {
            return totalThreads;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailCount() {
            return failCount;
        }

        public long getTotalTime() {
            return totalTime;
        }

        public double getQps() {
            return qps;
        }
    }
}
