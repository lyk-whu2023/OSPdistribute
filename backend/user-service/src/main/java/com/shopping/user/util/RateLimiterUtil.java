package com.shopping.user.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单的限流工具类（基于内存）
 */
public class RateLimiterUtil {
    
    // 存储每个 IP 的请求计数
    private static final ConcurrentHashMap<String, RequestCounter> counterMap = new ConcurrentHashMap<>();
    
    // 默认限制：60 秒内最多 10 次请求
    private static final int TIME_WINDOW = 60; // 时间窗口（秒）
    private static final int MAX_REQUESTS = 10; // 最大请求次数
    
    /**
     * 检查是否超过限流
     * @param ip IP 地址
     * @return true-未超过限流，false-超过限流
     */
    public static boolean isAllowed(String ip) {
        long currentTime = System.currentTimeMillis();
        
        RequestCounter counter = counterMap.computeIfAbsent(ip, k -> new RequestCounter());
        
        synchronized (counter) {
            // 如果超过时间窗口，重置计数
            if (currentTime - counter.startTime > TIME_WINDOW * 1000) {
                counter.count.set(0);
                counter.startTime = currentTime;
            }
            
            // 检查是否超过限制
            if (counter.count.incrementAndGet() > MAX_REQUESTS) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 检查是否超过限流（自定义参数）
     */
    public static boolean isAllowed(String ip, int timeWindow, int maxRequests) {
        long currentTime = System.currentTimeMillis();
        
        RequestCounter counter = counterMap.computeIfAbsent(ip, k -> new RequestCounter());
        
        synchronized (counter) {
            if (currentTime - counter.startTime > timeWindow * 1000) {
                counter.count.set(0);
                counter.startTime = currentTime;
            }
            
            if (counter.count.incrementAndGet() > maxRequests) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 清理过期的计数器（可定时调用）
     */
    public static void cleanup() {
        long currentTime = System.currentTimeMillis();
        counterMap.entrySet().removeIf(entry -> 
            currentTime - entry.getValue().startTime > TIME_WINDOW * 1000
        );
    }
    
    /**
     * 请求计数器
     */
    static class RequestCounter {
        AtomicInteger count = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();
    }
}
