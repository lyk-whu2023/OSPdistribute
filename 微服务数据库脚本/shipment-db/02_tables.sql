-- =====================================================
-- 物流服务 - 数据表结构
-- 包含表：shipments
-- 注意：移除了跨服务外键约束，改用逻辑关联
-- =====================================================

USE shipment_db;

-- =====================================================
-- 创建 shipments 表
-- =====================================================
DROP TABLE IF EXISTS shipments;

CREATE TABLE shipments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '物流 ID',
    order_id BIGINT NOT NULL UNIQUE COMMENT '订单 ID（逻辑关联）',
    shipping_company VARCHAR(50) NOT NULL COMMENT '快递公司',
    tracking_number VARCHAR(50) NOT NULL COMMENT '物流单号',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '物流状态（pending-待发货，shipping-配送中，delivered-已送达）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_tracking_number (tracking_number),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流信息表';

SELECT 'Shipment service tables created successfully!' AS result;
