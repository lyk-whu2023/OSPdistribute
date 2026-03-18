-- =====================================================
-- 店铺服务数据库创建脚本
-- 数据库名称：store_db
-- 创建时间：2026-03-18
-- 说明：店铺服务独立数据库，包含店铺信息、关注、评分
-- =====================================================

-- 如果数据库已存在，先删除
DROP DATABASE IF EXISTS store_db;

-- 创建数据库
CREATE DATABASE store_db 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE store_db;

-- 显示创建结果
SELECT 'Database store_db created successfully!' AS result;
