package com.shopping.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopping.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper//告诉 Spring/MyBatis 这个接口需要被扫描并生成代理实现类 。
public interface UserMapper extends BaseMapper<User>{

}
