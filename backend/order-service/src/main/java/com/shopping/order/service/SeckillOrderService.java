package com.shopping.order.service;

import com.shopping.order.entity.SeckillOrder;

public interface SeckillOrderService {
    
    SeckillOrder createSeckillOrder(Long seckillProductId, Long userId, Long addressId);
    
    SeckillOrder getSeckillOrderById(Long id);
    
    void updateSeckillOrderStatus(Long id, String status);
}
