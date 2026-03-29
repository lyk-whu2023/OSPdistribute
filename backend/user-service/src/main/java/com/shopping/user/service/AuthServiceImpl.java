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
import java.util.Random;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, Object> redisTemplate;
    
    // 验证码过期时间（分钟）
    private static final int CODE_EXPIRE_MINUTES = 5;
    // Token 黑名单前缀
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    // 验证码前缀
    private static final String VERIFY_CODE_PREFIX = "verify:code:";

    @Override
    @Transactional//保障用户插入操作的原子性
    public String register(RegisterRequest request) {
        validateRegisterRequest(request);
        
        // 验证验证码
        if (request.getVerifyCode() != null && !request.getVerifyCode().isEmpty()) {
            validateVerifyCode(request.getUsername(), request.getVerifyCode());
        }
        
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
        
        // 验证验证码
        if (request.getVerifyCode() != null && !request.getVerifyCode().isEmpty()) {
            validateVerifyCode(request.getUsername(), request.getVerifyCode());
        }
        
        // 检查 Token 是否在黑名单中
        if (request.getToken() != null && isTokenInBlacklist(request.getToken())) {
            log.warn("Token 已失效：{}", request.getToken().substring(0, Math.min(20, request.getToken().length())));
            throw new RuntimeException("Token 已失效，请重新登录");
        }
        
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
        // 将 Token 加入黑名单，防止重复使用
        addToTokenBlacklist(token);
        log.info("用户登出成功");
    }
    
    /**
     * 生成验证码（开发环境使用固定验证码 123456，方便测试）
     */
    public String generateVerifyCode(String key) {
        // 开发环境使用固定验证码，方便测试
        // String code = String.format("%06d", new Random().nextInt(999999));
        String code = "123456";
        String redisKey = VERIFY_CODE_PREFIX + key;
        redisTemplate.opsForValue().set(redisKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        log.info("验证码已生成并缓存，key: {}, code: {}", key, code);
        return code;
    }
    
    /**
     * 验证验证码
     */
    private void validateVerifyCode(String key, String code) {
        String redisKey = VERIFY_CODE_PREFIX + key;
        String storedCode = (String) redisTemplate.opsForValue().get(redisKey);
        
        if (storedCode == null) {
            log.warn("验证码已过期，key: {}", key);
            throw new RuntimeException("验证码已过期");
        }
        
        if (!storedCode.equals(code)) {
            log.warn("验证码错误，key: {}", key);
            throw new RuntimeException("验证码错误");
        }
        
        // 验证成功后删除验证码，防止重复使用
        redisTemplate.delete(redisKey);
        log.info("验证码验证成功，key: {}", key);
    }
    
    /**
     * 将 Token 加入黑名单
     */
    private void addToTokenBlacklist(String token) {
        // 解析 Token 获取过期时间
        Long expireTime = jwtUtil.getExpireTime(token);
        if (expireTime == null) {
            log.warn("无法解析 Token 过期时间");
            return;
        }
        
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        long ttl = expireTime - System.currentTimeMillis();
        
        if (ttl > 0) {
            redisTemplate.opsForValue().set(blacklistKey, "1", ttl, TimeUnit.MILLISECONDS);
            log.info("Token 已加入黑名单，剩余有效期：{}ms", ttl);
        }
    }
    
    /**
     * 检查 Token 是否在黑名单中
     */
    private boolean isTokenInBlacklist(String token) {
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        Boolean exists = redisTemplate.hasKey(blacklistKey);
        return exists != null && exists;
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