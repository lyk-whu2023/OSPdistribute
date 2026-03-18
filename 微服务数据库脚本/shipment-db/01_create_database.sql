-- =====================================================
-- 物流服务数据库创建脚本
-- 数据库名称：shipment_db
-- 创建时间：2026-03-18
-- 说明：物流服务独立数据库，包含物流信息
-- =====================================================

-- 如果数据库已存在，先删除
DROP DATABASE IF EXISTS shipment_db;

-- 创建数据库
CREATE DATABASE shipment_db 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE shipment_db;

-- 显示创建结果
SELECT 'Database shipment_db created successfully!' AS result;
