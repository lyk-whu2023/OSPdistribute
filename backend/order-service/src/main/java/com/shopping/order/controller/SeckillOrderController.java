package com.shopping.order.controller;

import com.shopping.order.entity.SeckillOrder;
import com.shopping.order.service.SeckillOrderService;
import com.shopping.order.service.impl.SeckillOrderServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 秒杀订单控制器
 * 
 * 提供以下 REST API：
 * 1. POST /seckill-orders - 创建秒杀订单
 * 2. GET /seckill-orders/{id} - 查询订单详情
 * 3. GET /seckill-orders/order/{orderNo} - 根据订单号查询
 * 4. GET /seckill-orders/user/{userId} - 查询用户订单列表
 * 5. PUT /seckill-orders/{id}/status - 更新订单状态
 * 
 * @author system
 * @since 2026-03-31
 */
@Slf4j
@RestController
@RequestMapping("/seckill-orders")
@RequiredArgsConstructor
public class SeckillOrderController {
    
    private final SeckillOrderService seckillOrderService;
    
    /**
     * 创建秒杀订单
     * 通常由 Kafka 消费者调用，不直接暴露给前端
     */
    @PostMapping
    public ResponseEntity<SeckillOrder> createSeckillOrder(
            @RequestParam Long seckillProductId,
            @RequestParam Long userId,
            @RequestParam Long addressId) {
        log.info("创建秒杀订单，seckillProductId: {}, userId: {}, addressId: {}", 
            seckillProductId, userId, addressId);
        
        SeckillOrder order = seckillOrderService.createSeckillOrder(
            seckillProductId,
            userId,
            addressId
        );
        return ResponseEntity.ok(order);
    }
    
    /**
     * 根据 ID 查询秒杀订单
     */
    @GetMapping("/{id}")
    public ResponseEntity<SeckillOrder> getSeckillOrderById(@PathVariable Long id) {
        log.info("查询秒杀订单，id: {}", id);
        SeckillOrder order = seckillOrderService.getSeckillOrderById(id);
        return ResponseEntity.ok(order);
    }
    
    /**
     * 根据订单号查询秒杀订单
     */
    @GetMapping("/order/{orderNo}")
    public ResponseEntity<SeckillOrder> getSeckillOrderByOrderNo(@PathVariable String orderNo) {
        log.info("查询秒杀订单，orderNo: {}", orderNo);
        SeckillOrder order = ((SeckillOrderServiceImpl)seckillOrderService).getSeckillOrderByOrderNo(orderNo);
        return ResponseEntity.ok(order);
    }
    
    /**
     * 根据用户 ID 查询秒杀订单列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SeckillOrder>> getSeckillOrdersByUserId(@PathVariable Long userId) {
        log.info("查询用户的秒杀订单列表，userId: {}", userId);
        List<SeckillOrder> orders = ((SeckillOrderServiceImpl)seckillOrderService).getSeckillOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * 更新秒杀订单状态
     * 
     * 支持的状态：
     * - PAID：已支付
     * - SHIPPED：已发货
     * - COMPLETED：已完成
     * - CANCELLED：已取消
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateSeckillOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("更新秒杀订单状态，id: {}, status: {}", id, status);
        seckillOrderService.updateSeckillOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
