-- =====================================================
-- 评论服务数据库创建脚本
-- 数据库名称：comment_db
-- 创建时间：2026-03-18
-- 说明：评论服务独立数据库，包含商品评论和收藏
-- =====================================================

-- 如果数据库已存在，先删除
DROP DATABASE IF EXISTS comment_db;

-- 创建数据库
CREATE DATABASE comment_db 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE comment_db;

-- 显示创建结果
SELECT 'Database comment_db created successfully!' AS result;
