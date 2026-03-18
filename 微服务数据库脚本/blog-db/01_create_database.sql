-- =====================================================
-- 博客服务数据库创建脚本
-- 数据库名称：blog_db
-- 创建时间：2026-03-18
-- 说明：博客服务独立数据库，包含博客文章、分类、标签、评论
-- =====================================================

-- 如果数据库已存在，先删除
DROP DATABASE IF EXISTS blog_db;

-- 创建数据库
CREATE DATABASE blog_db 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE blog_db;

-- 显示创建结果
SELECT 'Database blog_db created successfully!' AS result;
