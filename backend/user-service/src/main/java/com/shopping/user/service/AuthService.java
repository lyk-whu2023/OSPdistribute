package com.shopping.user.service;

import com.shopping.user.dto.request.LoginRequest;
import com.shopping.user.dto.request.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest request);
    String login(LoginRequest request);
    void logout(String token);
}