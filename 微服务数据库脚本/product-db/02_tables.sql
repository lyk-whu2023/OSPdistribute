-- =====================================================
-- 商品服务 - 数据表结构
-- 包含表：categories, products, product_images, product_skus,
--        product_attributes, product_attribute_values,
--        product_sku_attributes, seckill_products, seckill_activities
-- 注意：移除了跨服务外键约束，改用逻辑关联
-- =====================================================

USE product_db;

-- =====================================================
-- 创建 categories 表
-- =====================================================
DROP TABLE IF EXISTS categories;

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类 ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    parent_id BIGINT COMMENT '父分类 ID（逻辑关联）',
    level TINYINT NOT NULL COMMENT '分类级别',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序值',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_level (level),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- =====================================================
-- 创建 products 表
-- =====================================================
DROP TABLE IF EXISTS products;

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品 ID',
    store_id BIGINT NOT NULL COMMENT '店铺 ID（逻辑关联，无外键约束）',
    category_id BIGINT NOT NULL COMMENT '分类 ID（逻辑关联）',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    sales INT NOT NULL DEFAULT 0 COMMENT '销量',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-下架，1-上架）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_seckill TINYINT NOT NULL DEFAULT 0 COMMENT '是否秒杀商品（0-否，1-是）',
    INDEX idx_store_id (store_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_sales (sales DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品信息表';

-- =====================================================
-- 创建 product_images 表
-- =====================================================
DROP TABLE IF EXISTS product_images;

CREATE TABLE product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '图片 ID',
    product_id BIGINT NOT NULL COMMENT '商品 ID（逻辑关联）',
    image_url VARCHAR(255) NOT NULL COMMENT '图片 URL',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序值',
    is_main TINYINT NOT NULL DEFAULT 0 COMMENT '是否主图（0-否，1-是）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product_id (product_id),
    INDEX idx_is_main (is_main)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

-- =====================================================
-- 创建 seckill_activities 表
-- =====================================================
DROP TABLE IF EXISTS seckill_activities;

CREATE TABLE seckill_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '活动 ID',
    name VARCHAR(100) NOT NULL COMMENT '活动名称',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0-未开始，1-进行中，2-已结束）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='秒杀活动表';

-- =====================================================
-- 创建 seckill_products 表
-- =====================================================
DROP TABLE IF EXISTS seckill_products;

CREATE TABLE seckill_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '秒杀商品 ID',
    activity_id BIGINT NOT NULL COMMENT '活动 ID（逻辑关联）',
    product_id BIGINT NOT NULL COMMENT '商品 ID（逻辑关联）',
    seckill_price DECIMAL(10,2) NOT NULL COMMENT '秒杀价格',
    stock INT NOT NULL COMMENT '秒杀库存',
    sold INT NOT NULL DEFAULT 0 COMMENT '已售数量',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-下架，1-上架）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_activity_id (activity_id),
    INDEX idx_product_id (product_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='秒杀商品表';

-- =====================================================
-- 创建 product_skus 表
-- =====================================================
DROP TABLE IF EXISTS product_skus;

CREATE TABLE product_skus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规格 ID',
    product_id BIGINT NOT NULL COMMENT '商品 ID（逻辑关联）',
    sku_name VARCHAR(100) NOT NULL COMMENT '规格名称（如"黑色 256GB"）',
    price DECIMAL(10,2) NOT NULL COMMENT '规格价格',
    stock INT NOT NULL DEFAULT 0 COMMENT '规格库存',
    sales INT NOT NULL DEFAULT 0 COMMENT '规格销量',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品规格表';

-- =====================================================
-- 创建 product_attributes 表
-- =====================================================
DROP TABLE IF EXISTS product_attributes;

CREATE TABLE product_attributes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '属性 ID',
    product_id BIGINT NOT NULL COMMENT '商品 ID（逻辑关联）',
    attribute_name VARCHAR(50) NOT NULL COMMENT '属性名称（如"颜色"、"内存"）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品属性表';

-- =====================================================
-- 创建 product_attribute_values 表
-- =====================================================
DROP TABLE IF EXISTS product_attribute_values;

CREATE TABLE product_attribute_values (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '属性值 ID',
    attribute_id BIGINT NOT NULL COMMENT '属性 ID（逻辑关联）',
    value_name VARCHAR(50) NOT NULL COMMENT '属性值名称（如"黑色"、"256GB"）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_attribute_id (attribute_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品属性值表';

-- =====================================================
-- 创建 product_sku_attributes 表
-- =====================================================
DROP TABLE IF EXISTS product_sku_attributes;

CREATE TABLE product_sku_attributes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联 ID',
    sku_id BIGINT NOT NULL COMMENT '规格 ID（逻辑关联）',
    attribute_value_id BIGINT NOT NULL COMMENT '属性值 ID（逻辑关联）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_sku_id (sku_id),
    INDEX idx_attribute_value_id (attribute_value_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品规格属性关联表';

SELECT 'Product service tables created successfully!' AS result;
