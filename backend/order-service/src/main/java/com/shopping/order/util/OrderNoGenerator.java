package com.shopping.order.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 订单号生成器
 * 
 * 生成规则：
 * - 普通订单：ORD + 时间戳 + UUID(8 位)
 * - 秒杀订单：SECKILL + 时间戳 + UUID(8 位)
 * 
 * @author system
 * @since 2026-03-31
 */
public class OrderNoGenerator {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    /**
     * 生成普通订单号
     * 格式：ORDyyyyMMddHHmmss + UUID(8 位)
     * 
     * @return 订单号
     */
    public static String generate() {
        String timestamp = LocalDateTime.now().format(formatter);
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ORD" + timestamp + uuid;
    }
    
    /**
     * 生成秒杀订单号
     * 格式：SECKILLyyyyMMddHHmmss + UUID(8 位)
     * 
     * @return 秒杀订单号
     */
    public static String generateSeckill() {
        String timestamp = LocalDateTime.now().format(formatter);
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "SECKILL" + timestamp + uuid;
    }
    
    /**
     * 生成订单号（通用方法）
     * 默认生成普通订单号
     * 
     * @return 订单号
     */
    public static String generateOrderNo() {
        return generate();
    }
}
