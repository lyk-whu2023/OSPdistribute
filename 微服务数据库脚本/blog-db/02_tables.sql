-- =====================================================
-- 博客服务 - 数据表结构
-- 包含表：blog_categories, blog_tags, blogs, blog_comments
-- 注意：移除了跨服务外键约束，改用逻辑关联
-- =====================================================

USE blog_db;

-- =====================================================
-- 创建 blog_categories 表
-- =====================================================
DROP TABLE IF EXISTS blog_categories;

CREATE TABLE blog_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类 ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    parent_id BIGINT COMMENT '父分类 ID（逻辑关联）',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序值',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
    author_id BIGINT NOT NULL COMMENT '作者 ID（逻辑关联）',
    category_id BIGINT NOT NULL COMMENT '分类 ID（逻辑关联）',
    tags VARCHAR(255) COMMENT '标签（多个用逗号分隔）',
    view_count INT NOT NULL DEFAULT 0 COMMENT '阅读量',
    comment_count INT NOT NULL DEFAULT 0 COMMENT '评论数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-草稿，1-发布）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
    blog_id BIGINT NOT NULL COMMENT '博客 ID（逻辑关联）',
    user_id BIGINT NOT NULL COMMENT '用户 ID（逻辑关联）',
    content TEXT NOT NULL COMMENT '评论内容',
    parent_id BIGINT COMMENT '父评论 ID（逻辑关联）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_blog_id (blog_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客评论表';

SELECT 'Blog service tables created successfully!' AS result;
