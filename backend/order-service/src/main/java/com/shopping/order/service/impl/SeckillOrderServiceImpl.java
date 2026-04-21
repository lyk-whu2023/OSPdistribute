package com.shopping.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.order.entity.SeckillOrder;
import com.shopping.order.mapper.SeckillOrderMapper;
import com.shopping.order.service.SeckillOrderService;
import com.shopping.order.util.OrderNoGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 秒杀订单服务实现类
 * 
 * 功能说明：
 * 1. 创建秒杀订单 - 从 Kafka 消息中接收秒杀成功的消息，创建订单记录
 * 2. 查询秒杀订单 - 根据 ID 查询订单详情
 * 3. 更新订单状态 - 更新订单的支付、发货、完成等状态
 * 
 * @author system
 * @since 2026-03-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillOrderServiceImpl implements SeckillOrderService {

    private final SeckillOrderMapper seckillOrderMapper;

    /**
     * 创建秒杀订单
     * 
     * 业务逻辑：
     * 1. 生成订单号（使用雪花算法，保证全局唯一）
     * 2. 设置订单状态为 "PENDING"（待支付）
     * 3. 设置支付超时时间（30 分钟）
     * 4. 插入数据库
     * 
     * @param seckillProductId 秒杀商品 ID
     * @param userId 用户 ID
     * @param addressId 收货地址 ID
     * @return 创建的秒杀订单对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillOrder createSeckillOrder(Long seckillProductId, Long userId, Long addressId) {
        log.info("开始创建秒杀订单，seckillProductId: {}, userId: {}, addressId: {}", 
            seckillProductId, userId, addressId);
        
        try {
            // 1. 创建订单对象
            SeckillOrder seckillOrder = new SeckillOrder();
            
            // 2. 生成订单号（雪花算法）
            String orderNo = OrderNoGenerator.generateOrderNo();
            seckillOrder.setOrderNo(orderNo);
            log.info("生成订单号：{}", orderNo);
            
            // 3. 设置基本信息
            seckillOrder.setUserId(userId);
            seckillOrder.setSeckillProductId(seckillProductId);
            seckillOrder.setAddressId(addressId);
            
            // 4. 设置订单状态为待支付
            seckillOrder.setStatus("PENDING");
            
            // 5. 设置价格（需要在创建时传入，这里先设为 null）
            // seckillOrder.setPrice(price);
            
            // 6. 设置支付超时时间（30 分钟后）
            LocalDateTime payTimeout = LocalDateTime.now().plusMinutes(30);
            // seckillOrder.setPaymentTimeout(payTimeout);
            
            // 7. 设置创建时间
            seckillOrder.setCreateTime(LocalDateTime.now());
            seckillOrder.setUpdateTime(LocalDateTime.now());
            
            // 8. 插入数据库
            int inserted = seckillOrderMapper.insert(seckillOrder);
            
            if (inserted > 0) {
                log.info("秒杀订单创建成功，orderId: {}, orderNo: {}", seckillOrder.getId(), orderNo);
                return seckillOrder;
            } else {
                log.error("秒杀订单创建失败，seckillProductId: {}, userId: {}", seckillProductId, userId);
                throw new RuntimeException("创建秒杀订单失败");
            }
            
        } catch (Exception e) {
            log.error("创建秒杀订单异常，seckillProductId: {}, userId: {}, error: {}", 
                seckillProductId, userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据 ID 查询秒杀订单
     * 
     * @param id 订单 ID
     * @return 秒杀订单对象，如果不存在则返回 null
     */
    @Override
    public SeckillOrder getSeckillOrderById(Long id) {
        log.info("查询秒杀订单，id: {}", id);
        
        if (id == null) {
            log.warn("订单 ID 为空");
            return null;
        }
        
        SeckillOrder seckillOrder = seckillOrderMapper.selectById(id);
        
        if (seckillOrder != null) {
            log.info("查询秒杀订单成功，orderNo: {}", seckillOrder.getOrderNo());
        } else {
            log.warn("秒杀订单不存在，id: {}", id);
        }
        
        return seckillOrder;
    }

    /**
     * 根据订单号查询秒杀订单
     * 
     * @param orderNo 订单号
     * @return 秒杀订单对象，如果不存在则返回 null
     */
    public SeckillOrder getSeckillOrderByOrderNo(String orderNo) {
        log.info("根据订单号查询秒杀订单，orderNo: {}", orderNo);
        
        if (orderNo == null || orderNo.trim().isEmpty()) {
            log.warn("订单号为空");
            return null;
        }
        
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(
            new LambdaQueryWrapper<SeckillOrder>()
                .eq(SeckillOrder::getOrderNo, orderNo)
        );
        
        if (seckillOrder != null) {
            log.info("查询秒杀订单成功，id: {}", seckillOrder.getId());
        } else {
            log.warn("秒杀订单不存在，orderNo: {}", orderNo);
        }
        
        return seckillOrder;
    }

    /**
     * 更新秒杀订单状态
     * 
     * 状态流转：
     * PENDING（待支付）-> PAID（已支付）-> SHIPPED（已发货）-> COMPLETED（已完成）
     * PENDING（待支付）-> CANCELLED（已取消）
     * 
     * @param id 订单 ID
     * @param status 新状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSeckillOrderStatus(Long id, String status) {
        log.info("更新秒杀订单状态，id: {}, status: {}", id, status);
        
        if (id == null || status == null) {
            log.warn("订单 ID 或状态为空，id: {}, status: {}", id, status);
            throw new IllegalArgumentException("订单 ID 和状态不能为空");
        }
        
        SeckillOrder seckillOrder = seckillOrderMapper.selectById(id);
        if (seckillOrder == null) {
            log.warn("秒杀订单不存在，id: {}", id);
            throw new RuntimeException("订单不存在");
        }
        
        // 验证状态流转是否合法
        if (!isValidStatusTransition(seckillOrder.getStatus(), status)) {
            log.warn("非法的状态流转，orderId: {}, from: {}, to: {}", 
                id, seckillOrder.getStatus(), status);
            throw new RuntimeException("非法的订单状态流转");
        }
        
        // 更新状态
        seckillOrder.setStatus(status);
        seckillOrder.setUpdateTime(LocalDateTime.now());
        
        // 根据状态设置相应的时间
        switch (status) {
            case "PAID":
                seckillOrder.setPaymentTime(LocalDateTime.now());
                break;
            case "SHIPPED":
                seckillOrder.setShippingTime(LocalDateTime.now());
                break;
            case "COMPLETED":
                seckillOrder.setCompleteTime(LocalDateTime.now());
                break;
            case "CANCELLED":
                // 取消订单可能需要释放库存等逻辑
                log.info("订单已取消，orderId: {}", id);
                break;
            default:
                break;
        }
        
        int updated = seckillOrderMapper.updateById(seckillOrder);
        
        if (updated > 0) {
            log.info("秒杀订单状态更新成功，orderId: {}, newStatus: {}", id, status);
        } else {
            log.error("秒杀订单状态更新失败，orderId: {}", id);
            throw new RuntimeException("更新订单状态失败");
        }
    }

    /**
     * 验证订单状态流转是否合法
     * 
     * 合法的状态流转：
     * - PENDING -> PAID
     * - PENDING -> CANCELLED
     * - PAID -> SHIPPED
     * - SHIPPED -> COMPLETED
     * 
     * @param fromStatus 原状态
     * @param toStatus 新状态
     * @return 是否合法
     */
    private boolean isValidStatusTransition(String fromStatus, String toStatus) {
        if (fromStatus == null || toStatus == null) {
            return false;
        }
        
        switch (fromStatus) {
            case "PENDING":
                return "PAID".equals(toStatus) || "CANCELLED".equals(toStatus);
            case "PAID":
                return "SHIPPED".equals(toStatus);
            case "SHIPPED":
                return "COMPLETED".equals(toStatus);
            default:
                return false;
        }
    }

    /**
     * 根据用户 ID 查询秒杀订单列表
     * 
     * @param userId 用户 ID
     * @return 用户的秒杀订单列表
     */
    public java.util.List<SeckillOrder> getSeckillOrdersByUserId(Long userId) {
        log.info("查询用户的秒杀订单列表，userId: {}", userId);
        
        if (userId == null) {
            log.warn("用户 ID 为空");
            return new java.util.ArrayList<>();
        }
        
        java.util.List<SeckillOrder> orders = seckillOrderMapper.selectList(
            new LambdaQueryWrapper<SeckillOrder>()
                .eq(SeckillOrder::getUserId, userId)
                .orderByDesc(SeckillOrder::getCreateTime)
        );
        
        log.info("查询到 {} 个秒杀订单", orders != null ? orders.size() : 0);
        return orders;
    }
}
