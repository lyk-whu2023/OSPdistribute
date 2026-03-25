package com.shopping.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopping.order.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
    
}
