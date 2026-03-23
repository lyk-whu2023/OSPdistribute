package com.shopping.user.service;

import com.shopping.user.dto.response.UserResponse;
import com.shopping.user.entity.User;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, User user);
    void deleteUser(Long id);
}