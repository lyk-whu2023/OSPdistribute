package com.shopping.user.service;

import com.shopping.user.dto.request.LoginRequest;
import com.shopping.user.dto.request.RegisterRequest;
import com.shopping.user.dto.response.LoginResponse;

public interface AuthService {
    String register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout(String token);
}