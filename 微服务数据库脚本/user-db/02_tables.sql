-- =====================================================
-- 用户服务 - 数据表结构
-- 包含表：users, addresses
-- 注意：移除了跨服务外键约束，改用逻辑关联
-- =====================================================

USE user_db;

-- =====================================================
-- 创建 users 表
-- =====================================================
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户 ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像 URL',
    gender TINYINT COMMENT '性别（0-未知，1-男，2-女）',
    birthday DATE COMMENT '生日',
    register_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
    role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色（user, admin, store_admin）',
    INDEX idx_status (status),
    INDEX idx_role (role),
    INDEX idx_phone (phone),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- =====================================================
-- 创建 addresses 表
-- =====================================================
DROP TABLE IF EXISTS addresses;

CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地址 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID（逻辑关联，无外键约束）',
    name VARCHAR(20) NOT NULL COMMENT '收货人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '收货人手机号',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    detail VARCHAR(255) NOT NULL COMMENT '详细地址',
    is_default TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认地址（0-否，1-是）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

SELECT 'User service tables created successfully!' AS result;
