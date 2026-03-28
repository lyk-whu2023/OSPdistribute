package com.shopping.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.user.dto.request.LoginRequest;
import com.shopping.user.dto.request.RegisterRequest;
import com.shopping.user.dto.response.LoginResponse;
import com.shopping.user.dto.response.UserResponse;
import com.shopping.user.entity.User;
import com.shopping.user.mapper.UserMapper;
import com.shopping.user.util.JwtUtil;
import com.shopping.user.util.ValidationUtil;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor//自动创建构造函数
public class AuthServiceImpl implements AuthService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;//密码编码器
    private final JwtUtil jwtUtil;
    private final OperationLogService operationLogService;

    @Override
    @Transactional//保障用户插入操作的原子性
    public String register(RegisterRequest request) {
        validateRegisterRequest(request);
        
        User existingUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (existingUser != null) {
            log.warn("用户名已存在：{}", request.getUsername());
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        user.setRole("user");
        user.setRegisterTime(LocalDateTime.now());

        userMapper.insert(user);
        
        operationLogService.logRegister(user.getId(), user.getUsername(), "", "成功");
        log.info("用户注册成功：{}", user.getUsername());

        return "注册成功";
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        validateLoginRequest(request);
        
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("登录失败：用户名或密码错误 - {}", request.getUsername());
            throw new RuntimeException("用户名或者密码错误");
        }
        if (user.getStatus() == 0) {
            log.warn("登录失败：用户已被禁用 - {}", request.getUsername());
            throw new RuntimeException("用户已被禁用");
        }
        
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        operationLogService.logLogin(user.getId(), user.getUsername(), "", "成功");
        log.info("用户登录成功：{}", user.getUsername());
        
        // 构建响应对象
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        response.setUser(userResponse);
        
        return response;
    }

    @Override
    public void logout(String token) {
        jwtUtil.invalidateToken(token);
        log.info("用户登出成功");
    }
    
    private void validateRegisterRequest(RegisterRequest request) {
        if (!ValidationUtil.isValidUsername(request.getUsername())) {
            throw new RuntimeException("用户名格式不正确（3-20 位）");
        }
        if (!ValidationUtil.isValidPassword(request.getPassword())) {
            throw new RuntimeException("密码强度不足（至少 6 位，包含字母和数字）");
        }
        if (request.getPhone() != null && !ValidationUtil.isValidPhone(request.getPhone())) {
            throw new RuntimeException("手机号格式不正确");
        }
        if (request.getEmail() != null && !ValidationUtil.isValidEmail(request.getEmail())) {
            throw new RuntimeException("邮箱格式不正确");
        }
    }
    
    private void validateLoginRequest(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
    }
}