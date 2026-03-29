package com.shopping.user.service;

import com.shopping.user.dto.response.UserResponse;
import com.shopping.user.entity.User;
import com.shopping.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final OperationLogService operationLogService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserResponse getUserById(Long id) {
        String cacheKey = "user:info:" + id;
        
        // 尝试从缓存获取
        UserResponse cached = (UserResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("缓存命中，返回用户信息：{}", id);
            return cached;
        }
        
        // 缓存未命中，从数据库查询
        User user = userMapper.selectById(id);
        UserResponse response = convertToResponse(user);
        
        // 缓存结果，30 分钟过期
        if (response != null) {
            redisTemplate.opsForValue().set(cacheKey, response, 30, TimeUnit.MINUTES);
            log.info("用户信息已缓存：{}", id);
        }
        
        return response;
    }

    @Override
    public UserResponse updateUser(Long id, User user) {
        User existing = userMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setId(id);
        userMapper.updateById(user);
        
        User updated = userMapper.selectById(id);
        log.info("用户信息更新成功：{}", existing.getUsername());
        
        // 清除缓存
        String cacheKey = "user:info:" + id;
        redisTemplate.delete(cacheKey);
        log.info("用户缓存已清除：{}", id);
        
        return convertToResponse(updated);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        userMapper.deleteById(id);
        log.info("用户删除成功：{}", user.getUsername());
    }

    private UserResponse convertToResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}