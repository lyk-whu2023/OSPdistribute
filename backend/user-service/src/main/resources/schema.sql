-- 用户服务数据库表结构

-- 创建数据库
CREATE DATABASE IF NOT EXISTS user_db DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE user_db;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像 URL',
    gender INT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
    birthday DATETIME COMMENT '生日',
    register_time DATETIME COMMENT '注册时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    status INT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 地址表
CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地址 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    detail VARCHAR(255) NOT NULL COMMENT '详细地址',
    is_default INT DEFAULT 0 COMMENT '是否默认地址 0-否 1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_user_default (user_id, is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志 ID',
    user_id BIGINT COMMENT '用户 ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(50) COMMENT '操作名称',
    module VARCHAR(50) COMMENT '模块名称',
    method VARCHAR(50) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    result TEXT COMMENT '操作结果',
    ip VARCHAR(50) COMMENT 'IP 地址',
    duration BIGINT COMMENT '耗时（毫秒）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_username (username),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 插入测试数据
INSERT INTO users (username, password, phone, email, status, role, register_time) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iCjCzUWlR0N9qkF5u5h3jK9mZ8pL', '13800138000', 'admin@example.com', 1, 'admin', NOW());

INSERT INTO users (username, password, phone, email, status, role, register_time) 
VALUES ('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iCjCzUWlR0N9qkF5u5h3jK9mZ8pL', '13900139000', 'test@example.com', 1, 'user', NOW());

INSERT INTO addresses (user_id, name, phone, province, city, district, detail, is_default) 
VALUES (1, '管理员', '13800138000', '广东省', '深圳市', '南山区', '科技园南区', 1);

INSERT INTO addresses (user_id, name, phone, province, city, district, detail, is_default) 
VALUES (2, '测试用户', '13900139000', '北京市', '北京市', '朝阳区', '国贸 CBD', 1);
