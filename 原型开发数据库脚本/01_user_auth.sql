-- =====================================================
-- 传统分布式购物平台 - 用户与认证模块
-- 包含表：users, addresses, sms_templates, sms_records
-- 创建时间：2026-03-17
-- =====================================================

USE shopping_platform_db;

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
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- =====================================================
-- 创建 addresses 表
-- =====================================================
DROP TABLE IF EXISTS addresses;

CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地址 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    name VARCHAR(20) NOT NULL COMMENT '收货人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '收货人手机号',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    detail VARCHAR(255) NOT NULL COMMENT '详细地址',
    is_default TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认地址（0-否，1-是）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- =====================================================
-- 创建 sms_templates 表
-- =====================================================
DROP TABLE IF EXISTS sms_templates;

CREATE TABLE sms_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模板 ID',
    template_code VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编码',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    content VARCHAR(500) NOT NULL COMMENT '模板内容',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信模板表';

-- =====================================================
-- 创建 sms_records 表
-- =====================================================
DROP TABLE IF EXISTS sms_records;

CREATE TABLE sms_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录 ID',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    template_code VARCHAR(50) NOT NULL COMMENT '模板编码',
    content VARCHAR(500) NOT NULL COMMENT '发送内容',
    status TINYINT NOT NULL COMMENT '发送状态（0-失败，1-成功）',
    error_message VARCHAR(255) COMMENT '错误信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_phone (phone),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信发送记录表';

-- =====================================================
-- 插入默认数据
-- =====================================================

-- 插入默认管理员账户（密码为加密后的 123456）
INSERT INTO users (username, password, phone, email, nickname, role, status) 
VALUES ('admin', '$2a$10$XoLvF5C2dz9.7JHK8sN.TOxl9yYp4CqQKQ8MmqPHAKQYYQZQxZP8W', '13800138000', 'admin@shopping.com', '系统管理员', 'admin', 1);

-- 插入默认短信模板
INSERT INTO sms_templates (template_code, template_name, content, status) 
VALUES 
('LOGIN_CODE', '登录验证码', '您的登录验证码是：{code}，5 分钟内有效，请勿泄露给他人。', 1),
('REGISTER_CODE', '注册验证码', '您的注册验证码是：{code}，5 分钟内有效，请勿泄露给他人。', 1),
('ORDER_NOTICE', '订单通知', '您的订单{orderNo}已{status}，请注意查收。', 1);

SELECT 'User and authentication tables created successfully!' AS result;
