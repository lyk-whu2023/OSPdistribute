-- =====================================================
-- 订单服务数据库创建脚本
-- 数据库名称：order_db
-- 创建时间：2026-03-18
-- 说明：订单服务独立数据库，包含购物车、订单、秒杀订单等
-- =====================================================

-- 如果数据库已存在，先删除
DROP DATABASE IF EXISTS order_db;

-- 创建数据库
CREATE DATABASE order_db 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE order_db;

-- 显示创建结果
SELECT 'Database order_db created successfully!' AS result;
