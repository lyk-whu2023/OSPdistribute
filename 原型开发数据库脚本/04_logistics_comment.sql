-- =====================================================
-- 传统分布式购物平台 - 物流与评论模块
-- 包含表：shipments, comments, favorites
-- 创建时间：2026-03-17
-- =====================================================

USE shopping_platform_db;

-- =====================================================
-- 创建 shipments 表
-- =====================================================
DROP TABLE IF EXISTS shipments;

CREATE TABLE shipments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '物流 ID',
    order_id BIGINT NOT NULL UNIQUE COMMENT '订单 ID',
    shipping_company VARCHAR(50) NOT NULL COMMENT '快递公司',
    tracking_number VARCHAR(50) NOT NULL COMMENT '物流单号',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '物流状态（pending-待发货，shipping-配送中，delivered-已送达）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_tracking_number (tracking_number),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流信息表';

-- =====================================================
-- 创建 comments 表
-- =====================================================
DROP TABLE IF EXISTS comments;

CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    product_id BIGINT NOT NULL COMMENT '商品 ID',
    order_item_id BIGINT NOT NULL COMMENT '订单商品项 ID',
    content TEXT NOT NULL COMMENT '评论内容',
    rating TINYINT NOT NULL COMMENT '评分（1-5 星）',
    image_urls VARCHAR(500) COMMENT '评论图片 URL（多个用逗号分隔）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评论表';

-- =====================================================
-- 创建 favorites 表
-- =====================================================
DROP TABLE IF EXISTS favorites;

CREATE TABLE favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    product_id BIGINT NOT NULL COMMENT '商品 ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_product (user_id, product_id) COMMENT '防止重复收藏',
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

SELECT 'Logistics and comment tables created successfully!' AS result;
