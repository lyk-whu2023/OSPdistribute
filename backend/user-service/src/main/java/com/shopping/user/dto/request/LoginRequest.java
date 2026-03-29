package com.shopping.user.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String verifyCode; // 验证码（可选）
    private String token; // Token（用于检查黑名单）
}