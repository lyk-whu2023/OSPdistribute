-- =====================================================
-- 传统分布式购物平台 - 数据库创建脚本
-- 数据库名称：shopping_platform_db
-- 创建时间：2026-03-17
-- =====================================================

-- 如果数据库已存在，先删除
DROP DATABASE IF EXISTS shopping_platform_db;

-- 创建数据库
CREATE DATABASE shopping_platform_db 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE shopping_platform_db;

-- 显示创建结果
SELECT 'Database shopping_platform_db created successfully!' AS result;
