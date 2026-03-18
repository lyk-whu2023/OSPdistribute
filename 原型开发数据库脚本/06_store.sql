-- =====================================================
-- 传统分布式购物平台 - 店铺模块
-- 包含表：stores, store_follows, store_ratings
-- 注意：此脚本应在 products 表创建之后执行
-- 创建时间：2026-03-17
-- =====================================================

USE shopping_platform_db;

-- =====================================================
-- 创建 stores 表
-- =====================================================
DROP TABLE IF EXISTS stores;

CREATE TABLE stores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '店铺 ID',
    store_name VARCHAR(100) NOT NULL UNIQUE COMMENT '店铺名称',
    owner_id BIGINT NOT NULL COMMENT '店主 ID',
    logo VARCHAR(255) COMMENT '店铺 logo',
    description TEXT COMMENT '店铺描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-关闭，1-营业）',
    follow_count INT NOT NULL DEFAULT 0 COMMENT '关注数',
    rating DECIMAL(3,1) NOT NULL DEFAULT 0 COMMENT '评分',
    rating_count INT NOT NULL DEFAULT 0 COMMENT '评分数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_owner_id (owner_id),
    INDEX idx_status (status),
    INDEX idx_follow_count (follow_count DESC),
    INDEX idx_rating (rating DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺信息表';

-- =====================================================
-- 创建 store_follows 表
-- =====================================================
DROP TABLE IF EXISTS store_follows;

CREATE TABLE store_follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关注 ID',
    store_id BIGINT NOT NULL COMMENT '店铺 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    follow_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_store_user (store_id, user_id) COMMENT '防止重复关注',
    INDEX idx_store_id (store_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺关注表';

-- =====================================================
-- 创建 store_ratings 表
-- =====================================================
DROP TABLE IF EXISTS store_ratings;

CREATE TABLE store_ratings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评分 ID',
    store_id BIGINT NOT NULL COMMENT '店铺 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    rating TINYINT NOT NULL COMMENT '评分（1-5 星）',
    comment TEXT COMMENT '评论内容',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_store_user (store_id, user_id) COMMENT '防止重复评分',
    INDEX idx_store_id (store_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺评分表';

-- =====================================================
-- 添加 products 表的外键约束（在 stores 表创建后）
-- =====================================================
ALTER TABLE products 
ADD CONSTRAINT fk_products_store_id 
FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE RESTRICT;

-- =====================================================
-- 插入默认数据
-- =====================================================

-- 插入默认店铺（需要先有店主用户）
INSERT INTO stores (store_name, owner_id, description, status, follow_count, rating, rating_count) 
VALUES 
('官方旗舰店', 1, '官方自营店铺，品质保证', 1, 0, 5.0, 0),
('数码专营店', 1, '专业数码产品销售', 1, 0, 4.8, 0);

SELECT 'Store tables created successfully!' AS result;
