package com.shopping.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String nickname;
    private String avatar;//存储头像图片url
    private Integer gender;
    private LocalDateTime birthday;
    private LocalDateTime registerTime;
    private LocalDateTime lastLoginTime;
    private Integer status;
    private String role;
}