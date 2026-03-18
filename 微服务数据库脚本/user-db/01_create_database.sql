-- =====================================================
-- 用户服务数据库创建脚本
-- 数据库名称：user_db
-- 创建时间：2026-03-18
-- 说明：用户服务独立数据库，包含用户信息和地址管理
-- =====================================================

-- 如果数据库已存在，先删除
DROP DATABASE IF EXISTS user_db;

-- 创建数据库
CREATE DATABASE user_db 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE user_db;

-- 显示创建结果
SELECT 'Database user_db created successfully!' AS result;
