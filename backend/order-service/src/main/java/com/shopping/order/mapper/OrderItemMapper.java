package com.shopping.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopping.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    
}
