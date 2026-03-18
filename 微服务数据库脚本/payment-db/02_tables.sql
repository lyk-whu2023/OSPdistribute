-- =====================================================
-- 支付服务 - 数据表结构
-- 包含表：payments
-- 注意：移除了跨服务外键约束，改用逻辑关联
-- =====================================================

USE payment_db;

-- =====================================================
-- 创建 payments 表
-- =====================================================
DROP TABLE IF EXISTS payments;

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '支付记录 ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号（逻辑关联）',
    user_id BIGINT NOT NULL COMMENT '用户 ID（逻辑关联）',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_method VARCHAR(20) NOT NULL COMMENT '支付方式（alipay, wechat, unionpay）',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '支付状态（pending-待支付，success-成功，failed-失败）',
    transaction_id VARCHAR(100) COMMENT '交易 ID（第三方支付流水号）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_payment_status (payment_status),
    INDEX idx_transaction_id (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

SELECT 'Payment service tables created successfully!' AS result;
