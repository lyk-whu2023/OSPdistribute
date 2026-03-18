-- =====================================================
-- 店铺服务 - 数据表结构
-- 包含表：stores, store_follows, store_ratings
-- 注意：移除了跨服务外键约束，改用逻辑关联
-- =====================================================

USE store_db;

-- =====================================================
-- 创建 stores 表
-- =====================================================
DROP TABLE IF EXISTS stores;

CREATE TABLE stores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '店铺 ID',
    store_name VARCHAR(100) NOT NULL UNIQUE COMMENT '店铺名称',
    owner_id BIGINT NOT NULL COMMENT '店主 ID（逻辑关联）',
    logo VARCHAR(255) COMMENT '店铺 logo',
    description TEXT COMMENT '店铺描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-关闭，1-营业）',
    follow_count INT NOT NULL DEFAULT 0 COMMENT '关注数',
    rating DECIMAL(3,1) NOT NULL DEFAULT 0 COMMENT '评分',
    rating_count INT NOT NULL DEFAULT 0 COMMENT '评分数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
    store_id BIGINT NOT NULL COMMENT '店铺 ID（逻辑关联）',
    user_id BIGINT NOT NULL COMMENT '用户 ID（逻辑关联）',
    follow_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    INDEX idx_store_id (store_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺关注表';

-- 添加唯一索引防止重复关注（可选，如果需要应用层控制可以去掉）
-- ALTER TABLE store_follows ADD UNIQUE INDEX uk_store_user (store_id, user_id);

-- =====================================================
-- 创建 store_ratings 表
-- =====================================================
DROP TABLE IF EXISTS store_ratings;

CREATE TABLE store_ratings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评分 ID',
    store_id BIGINT NOT NULL COMMENT '店铺 ID（逻辑关联）',
    user_id BIGINT NOT NULL COMMENT '用户 ID（逻辑关联）',
    rating TINYINT NOT NULL COMMENT '评分（1-5 星）',
    comment TEXT COMMENT '评论内容',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_store_id (store_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='店铺评分表';

-- 添加唯一索引防止重复评分（可选，如果需要应用层控制可以去掉）
-- ALTER TABLE store_ratings ADD UNIQUE INDEX uk_store_user (store_id, user_id);

SELECT 'Store service tables created successfully!' AS result;
