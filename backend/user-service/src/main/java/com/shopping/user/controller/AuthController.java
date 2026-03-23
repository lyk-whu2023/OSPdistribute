package com.shopping.user.controller;

import com.shopping.user.dto.request.LoginRequest;
import com.shopping.user.dto.request.RegisterRequest;
import com.shopping.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController//@Controller 和 @ResponseBody 组合注解
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String token) {
        authService.logout(token.replace("Bearer ", ""));
    }
}