-- 创建订单数据库
CREATE DATABASE IF NOT EXISTS order_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE order_db;

-- 创建购物车表
CREATE TABLE IF NOT EXISTS `carts` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 创建购物车商品表
CREATE TABLE IF NOT EXISTS `cart_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车商品项 ID',
    `cart_id` BIGINT NOT NULL COMMENT '购物车 ID',
    `product_id` BIGINT NOT NULL COMMENT '商品 ID',
    `sku_id` BIGINT DEFAULT NULL COMMENT '商品规格 ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '商品数量',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_cart_id` (`cart_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车商品表';

-- 创建订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单 ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `address_id` BIGINT NOT NULL COMMENT '地址 ID',
    `store_id` BIGINT NOT NULL COMMENT '店铺 ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额',
    `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实际支付金额',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态',
    `payment_method` VARCHAR(20) DEFAULT NULL COMMENT '支付方式',
    `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `shipping_time` DATETIME DEFAULT NULL COMMENT '发货时间',
    `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_store_id` (`store_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 创建订单商品表
CREATE TABLE IF NOT EXISTS `order_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单商品项 ID',
    `order_id` BIGINT NOT NULL COMMENT '订单 ID',
    `product_id` BIGINT NOT NULL COMMENT '商品 ID',
    `sku_id` BIGINT DEFAULT NULL COMMENT '商品规格 ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `sku_name` VARCHAR(100) DEFAULT NULL COMMENT '商品规格名称',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `quantity` INT NOT NULL COMMENT '商品数量',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品表';

-- 创建秒杀订单表
CREATE TABLE IF NOT EXISTS `seckill_orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '秒杀订单 ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `address_id` BIGINT NOT NULL COMMENT '地址 ID',
    `seckill_product_id` BIGINT NOT NULL COMMENT '秒杀商品 ID',
    `product_id` BIGINT NOT NULL COMMENT '商品 ID',
    `price` DECIMAL(10,2) NOT NULL COMMENT '秒杀价格',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态',
    `payment_method` VARCHAR(20) DEFAULT NULL COMMENT '支付方式',
    `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `shipping_time` DATETIME DEFAULT NULL COMMENT '发货时间',
    `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_seckill_product_id` (`seckill_product_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='秒杀订单表';
