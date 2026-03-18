-- =====================================================
-- 传统分布式购物平台 - 内容与统计模块
-- 包含表：blogs, blog_categories, blog_tags, blog_comments, stats_records
-- 创建时间：2026-03-17
-- =====================================================

USE shopping_platform_db;

-- =====================================================
-- 创建 blog_categories 表
-- =====================================================
DROP TABLE IF EXISTS blog_categories;

CREATE TABLE blog_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类 ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    parent_id BIGINT COMMENT '父分类 ID',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序值',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (parent_id) REFERENCES blog_categories(id) ON DELETE SET NULL,
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客分类表';

-- =====================================================
-- 创建 blog_tags 表
-- =====================================================
DROP TABLE IF EXISTS blog_tags;

CREATE TABLE blog_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签 ID',
    name VARCHAR(30) NOT NULL UNIQUE COMMENT '标签名称',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客标签表';

-- =====================================================
-- 创建 blogs 表
-- =====================================================
DROP TABLE IF EXISTS blogs;

CREATE TABLE blogs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '博客 ID',
    title VARCHAR(100) NOT NULL COMMENT '博客标题',
    content LONGTEXT NOT NULL COMMENT '博客内容',
    author_id BIGINT NOT NULL COMMENT '作者 ID',
    category_id BIGINT NOT NULL COMMENT '分类 ID',
    tags VARCHAR(255) COMMENT '标签（多个用逗号分隔）',
    view_count INT NOT NULL DEFAULT 0 COMMENT '阅读量',
    comment_count INT NOT NULL DEFAULT 0 COMMENT '评论数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-草稿，1-发布）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES blog_categories(id) ON DELETE RESTRICT,
    INDEX idx_author_id (author_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_view_count (view_count DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客文章表';

-- =====================================================
-- 创建 blog_comments 表
-- =====================================================
DROP TABLE IF EXISTS blog_comments;

CREATE TABLE blog_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论 ID',
    blog_id BIGINT NOT NULL COMMENT '博客 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    content TEXT NOT NULL COMMENT '评论内容',
    parent_id BIGINT COMMENT '父评论 ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES blog_comments(id) ON DELETE CASCADE,
    INDEX idx_blog_id (blog_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客评论表';

-- =====================================================
-- 创建 stats_records 表
-- =====================================================
DROP TABLE IF EXISTS stats_records;

CREATE TABLE stats_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录 ID',
    stats_type VARCHAR(50) NOT NULL COMMENT '统计类型（sales, user, product, seckill, blog）',
    stats_date DATE NOT NULL COMMENT '统计日期',
    stats_value DECIMAL(20,2) NOT NULL COMMENT '统计值',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_stats_type (stats_type),
    INDEX idx_stats_date (stats_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统计记录表';

-- =====================================================
-- 插入默认数据
-- =====================================================

-- 插入默认博客分类
INSERT INTO blog_categories (name, parent_id, sort) VALUES
('技术文章', NULL, 1),
('购物指南', NULL, 2),
('生活分享', NULL, 3),
('前端开发', 1, 1),
('后端开发', 1, 2);

-- 插入默认博客标签
INSERT INTO blog_tags (name) VALUES
('Java'),
('Vue'),
('购物'),
('教程'),
('技巧');

SELECT 'Content and statistics tables created successfully!' AS result;
