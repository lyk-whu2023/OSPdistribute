-- =====================================================
-- 传统分布式购物平台 - 订单与支付模块
-- 包含表：carts, cart_items, orders, order_items, seckill_orders, payments
-- 创建时间：2026-03-17
-- =====================================================

USE shopping_platform_db;

-- =====================================================
-- 创建 carts 表
-- =====================================================
DROP TABLE IF EXISTS carts;

CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车 ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户 ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- =====================================================
-- 创建 cart_items 表
-- =====================================================
DROP TABLE IF EXISTS cart_items;

CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车商品项 ID',
    cart_id BIGINT NOT NULL COMMENT '购物车 ID',
    product_id BIGINT NOT NULL COMMENT '商品 ID',
    sku_id BIGINT COMMENT '商品规格 ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '商品数量',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (sku_id) REFERENCES product_skus(id) ON DELETE SET NULL,
    INDEX idx_cart_id (cart_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车商品项表';

-- =====================================================
-- 创建 orders 表
-- =====================================================
DROP TABLE IF EXISTS orders;

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单 ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    address_id BIGINT NOT NULL COMMENT '地址 ID',
    store_id BIGINT NOT NULL COMMENT '店铺 ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实际支付金额',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态（pending-待支付，paid-已支付，shipping-配送中，completed-已完成，cancelled-已取消）',
    payment_method VARCHAR(20) COMMENT '支付方式',
    payment_time DATETIME COMMENT '支付时间',
    shipping_time DATETIME COMMENT '发货时间',
    complete_time DATETIME COMMENT '完成时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE RESTRICT,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- =====================================================
-- 创建 order_items 表
-- =====================================================
DROP TABLE IF EXISTS order_items;

CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单商品项 ID',
    order_id BIGINT NOT NULL COMMENT '订单 ID',
    product_id BIGINT NOT NULL COMMENT '商品 ID',
    sku_id BIGINT COMMENT '商品规格 ID',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    sku_name VARCHAR(100) COMMENT '商品规格名称',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    quantity INT NOT NULL COMMENT '商品数量',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    FOREIGN KEY (sku_id) REFERENCES product_skus(id) ON DELETE SET NULL,
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品项表';

-- =====================================================
-- 创建 seckill_orders 表
-- =====================================================
DROP TABLE IF EXISTS seckill_orders;

CREATE TABLE seckill_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '秒杀订单 ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    address_id BIGINT NOT NULL COMMENT '地址 ID',
    seckill_product_id BIGINT NOT NULL COMMENT '秒杀商品 ID',
    product_id BIGINT NOT NULL COMMENT '商品 ID',
    price DECIMAL(10,2) NOT NULL COMMENT '秒杀价格',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态（pending-待支付，paid-已支付，shipping-配送中，completed-已完成，cancelled-已取消）',
    payment_method VARCHAR(20) COMMENT '支付方式',
    payment_time DATETIME COMMENT '支付时间',
    shipping_time DATETIME COMMENT '发货时间',
    complete_time DATETIME COMMENT '完成时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE RESTRICT,
    FOREIGN KEY (seckill_product_id) REFERENCES seckill_products(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='秒杀订单表';

-- =====================================================
-- 创建 payments 表
-- =====================================================
DROP TABLE IF EXISTS payments;

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '支付记录 ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_method VARCHAR(20) NOT NULL COMMENT '支付方式',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '支付状态（pending-待支付，success-成功，failed-失败）',
    transaction_id VARCHAR(100) COMMENT '交易 ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_user_id (user_id),
    INDEX idx_payment_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

SELECT 'Order and payment tables created successfully!' AS result;
