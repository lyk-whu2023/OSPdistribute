-- =====================================================
-- 评论服务 - 数据表结构
-- 包含表：comments, favorites
-- 注意：移除了跨服务外键约束，改用逻辑关联
-- =====================================================

USE comment_db;

-- =====================================================
-- 创建 comments 表
-- =====================================================
DROP TABLE IF EXISTS comments;

CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID（逻辑关联）',
    product_id BIGINT NOT NULL COMMENT '商品 ID（逻辑关联）',
    order_item_id BIGINT NOT NULL COMMENT '订单商品项 ID（逻辑关联）',
    content TEXT NOT NULL COMMENT '评论内容',
    rating TINYINT NOT NULL COMMENT '评分（1-5 星）',
    image_urls VARCHAR(500) COMMENT '评论图片 URL（多个用逗号分隔）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评论表';

-- =====================================================
-- 创建 favorites 表
-- =====================================================
DROP TABLE IF EXISTS favorites;

CREATE TABLE favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID（逻辑关联）',
    product_id BIGINT NOT NULL COMMENT '商品 ID（逻辑关联）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 添加唯一索引防止重复收藏（可选，如果需要应用层控制可以去掉）
-- ALTER TABLE favorites ADD UNIQUE INDEX uk_user_product (user_id, product_id);

SELECT 'Comment service tables created successfully!' AS result;
