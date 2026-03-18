-- =====================================================
-- 商品服务数据库创建脚本
-- 数据库名称：product_db
-- 创建时间：2026-03-18
-- 说明：商品服务独立数据库，包含商品、分类、库存、秒杀等
-- =====================================================

-- 如果数据库已存在，先删除
DROP DATABASE IF EXISTS product_db;

-- 创建数据库
CREATE DATABASE product_db 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE product_db;

-- 显示创建结果
SELECT 'Database product_db created successfully!' AS result;
