package com.shopping.user.service;

import com.shopping.user.dto.response.UserResponse;
import com.shopping.user.entity.User;
import com.shopping.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final OperationLogService operationLogService;

    @Override
    public UserResponse getUserById(Long id) {
        User user = userMapper.selectById(id);
        return convertToResponse(user);
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