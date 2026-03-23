package com.shopping.user.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String nickname;
    private String avatar;
    private String gender;
    private LocalDateTime birthday;
    private LocalDateTime registerTime;
    private LocalDateTime lastLoginTime;
    private String role;
}