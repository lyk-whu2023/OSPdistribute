package com.shopping.user.controller;

import com.shopping.user.dto.request.LoginRequest;
import com.shopping.user.dto.request.RegisterRequest;
import com.shopping.user.dto.response.LoginResponse;
import com.shopping.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String token) {
        authService.logout(token.replace("Bearer ", ""));
    }
    
    /**
     * 获取验证码（用于性能测试）
     */
    @GetMapping("/verify-code")
    public String getVerifyCode(@RequestParam String key) {
        log.info("请求验证码，key: {}", key);
        String code = authService.generateVerifyCode(key);
        return code; // 直接返回验证码，方便测试
    }
}