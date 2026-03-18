-- =====================================================
-- 订单服务 - 初始化数据
-- =====================================================

USE order_db;

-- =====================================================
-- 插入购物车数据
-- =====================================================
INSERT INTO carts (user_id) 
VALUES 
(1),
(2);

-- =====================================================
-- 插入购物车商品项数据
-- =====================================================
INSERT INTO cart_items (cart_id, product_id, sku_id, quantity, price) 
VALUES 
(1, 1, 1, 1, 8999.00),
(1, 3, NULL, 2, 299.00),
(2, 2, 4, 1, 14999.00);

-- =====================================================
-- 插入订单数据
-- =====================================================
INSERT INTO orders (order_no, user_id, address_id, store_id, total_amount, actual_amount, status, payment_method, payment_time) 
VALUES 
('ORD202603180001', 1, 1, 1, 9298.00, 9298.00, 'completed', 'alipay', '2026-03-18 10:30:00'),
('ORD202603180002', 2, 2, 1, 14999.00, 14999.00, 'paid', 'wechat', '2026-03-18 11:00:00');

-- =====================================================
-- 插入订单商品项数据
-- =====================================================
INSERT INTO order_items (order_id, product_id, sku_id, product_name, sku_name, price, quantity) 
VALUES 
(1, 1, 1, 'iPhone 15 Pro', '黑色 256GB', 8999.00, 1),
(1, 3, NULL, '商务休闲衬衫', NULL, 299.00, 2),
(2, 2, 4, 'MacBook Pro 14', '深空灰 512GB', 14999.00, 1);

-- =====================================================
-- 插入秒杀订单数据
-- =====================================================
INSERT INTO seckill_orders (order_no, user_id, address_id, seckill_product_id, product_id, price, status) 
VALUES 
('SEC202603180001', 1, 1, 1, 4, 199.00, 'pending');

SELECT 'Order service init data inserted successfully!' AS result;
