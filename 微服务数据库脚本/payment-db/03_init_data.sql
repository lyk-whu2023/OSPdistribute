-- =====================================================
-- 支付服务 - 初始化数据
-- =====================================================

USE payment_db;

-- =====================================================
-- 插入支付记录数据
-- =====================================================
INSERT INTO payments (order_no, user_id, amount, payment_method, payment_status, transaction_id) 
VALUES 
('ORD202603180001', 1, 9298.00, 'alipay', 'success', 'ALI2026031810300001'),
('ORD202603180002', 2, 14999.00, 'wechat', 'success', 'WX2026031811000001'),
('SEC202603180001', 1, 199.00, 'alipay', 'pending', NULL);

SELECT 'Payment service init data inserted successfully!' AS result;
