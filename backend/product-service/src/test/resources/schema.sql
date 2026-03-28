-- H2 数据库 Schema for Product Service Tests

-- 商品分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    parent_id BIGINT,
    sort INT DEFAULT 0,
    status INT DEFAULT 1,
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

-- 商品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    sales INT NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 1,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    is_seckill INT DEFAULT 0
);

-- 商品图片表
CREATE TABLE IF NOT EXISTS product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    sort INT DEFAULT 0,
    is_main INT DEFAULT 0,
    create_time TIMESTAMP
);

-- 秒杀活动表
CREATE TABLE IF NOT EXISTS seckill_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status INT DEFAULT 1,
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

-- 秒杀商品表
CREATE TABLE IF NOT EXISTS seckill_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    seckill_price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    sold INT NOT NULL DEFAULT 0,
    status INT DEFAULT 1,
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

-- 商品 SKU 表
CREATE TABLE IF NOT EXISTS product_skus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku_name VARCHAR(200),
    price DECIMAL(10,2),
    stock INT,
    sku_attrs VARCHAR(1000),
    create_time TIMESTAMP
);

-- 商品属性表
CREATE TABLE IF NOT EXISTS product_attributes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    attr_values VARCHAR(1000),
    create_time TIMESTAMP
);

-- 商品属性值表
CREATE TABLE IF NOT EXISTS product_attribute_values (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    attribute_id BIGINT NOT NULL,
    attr_value VARCHAR(200),
    create_time TIMESTAMP
);
