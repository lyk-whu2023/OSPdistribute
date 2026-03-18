-- =====================================================
-- 物流服务 - 初始化数据
-- =====================================================

USE shipment_db;

-- =====================================================
-- 插入物流数据
-- =====================================================
-- 注意：order_id 应该是订单表中的主键 ID（BIGINT），不是订单号
-- 这里假设订单 ID 为 1 和 2
INSERT INTO shipments (order_id, shipping_company, tracking_number, status) 
VALUES 
(1, '顺丰速运', 'SF13800138000', 'delivered'),
(2, '中通快递', 'ZT13900139000', 'shipping');

SELECT 'Shipment service init data inserted successfully!' AS result;
