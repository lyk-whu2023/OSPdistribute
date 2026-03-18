-- =====================================================
-- 支付服务数据库创建脚本
-- 数据库名称：payment_db
-- 创建时间：2026-03-18
-- 说明：支付服务独立数据库，包含支付记录
-- =====================================================

-- 如果数据库已存在，先删除
DROP DATABASE IF EXISTS payment_db;

-- 创建数据库
CREATE DATABASE payment_db 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE payment_db;

-- 显示创建结果
SELECT 'Database payment_db created successfully!' AS result;
