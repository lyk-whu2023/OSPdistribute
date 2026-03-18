-- =====================================================
-- 短信服务数据库创建脚本
-- 数据库名称：sms_db
-- 创建时间：2026-03-18
-- 说明：短信服务独立数据库，包含短信模板和发送记录
-- =====================================================

-- 如果数据库已存在，先删除
DROP DATABASE IF EXISTS sms_db;

-- 创建数据库
CREATE DATABASE sms_db 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE sms_db;

-- 显示创建结果
SELECT 'Database sms_db created successfully!' AS result;
