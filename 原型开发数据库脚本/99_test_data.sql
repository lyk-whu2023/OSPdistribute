-- =====================================================
-- 传统分布式购物平台 - 测试数据插入脚本
-- 用于插入模拟数据，方便开发和测试
-- 创建时间：2026-03-17
-- =====================================================

USE shopping_platform_db;

-- =====================================================
-- 插入测试用户
-- =====================================================
INSERT INTO users (username, password, phone, email, nickname, gender, role, status) VALUES
('user1', '$2a$10$XoLvF5C2dz9.7JHK8sN.TOxl9yYp4CqQKQ8MmqPHAKQYYQZQxZP8W', '13800138001', 'user1@test.com', '测试用户 1', 1, 'user', 1),
('user2', '$2a$10$XoLvF5C2dz9.7JHK8sN.TOxl9yYp4CqQKQ8MmqPHAKQYYQZQxZP8W', '13800138002', 'user2@test.com', '测试用户 2', 2, 'user', 1),
('store_user', '$2a$10$XoLvF5C2dz9.7JHK8sN.TOxl9yYp4CqQKQ8MmqPHAKQYYQZQxZP8W', '13800138003', 'store@test.com', '店铺管理员', 1, 'store_admin', 1);

-- =====================================================
-- 插入测试地址
-- =====================================================
INSERT INTO addresses (user_id, name, phone, province, city, district, detail, is_default) VALUES
(2, '张三', '13800138001', '广东省', '深圳市', '南山区', '科技园南区 1 栋 101', 1),
(2, '张三', '13800138001', '广东省', '深圳市', '福田区', '福田中心区 2 栋 202', 0),
(3, '李四', '13800138002', '北京市', '北京市', '朝阳区', '朝阳公园路 3 号', 1);

-- =====================================================
-- 插入测试店铺（更新店主 ID）
-- =====================================================
UPDATE stores SET owner_id = 4 WHERE id = 1;
UPDATE stores SET owner_id = 4 WHERE id = 2;

-- =====================================================
-- 插入测试商品
-- =====================================================
INSERT INTO products (store_id, category_id, name, description, price, stock, sales, status, is_seckill) VALUES
(1, 2, '智能手机 X1', '高性能智能手机，6.7 英寸屏幕，128GB 存储', 3999.00, 100, 1200, 1, 0),
(1, 2, '智能手机 Pro', '旗舰级智能手机，256GB 存储，5G 网络', 5999.00, 50, 800, 1, 0),
(1, 3, '轻薄笔记本电脑', '14 英寸轻薄本，适合办公和学习', 4999.00, 30, 500, 1, 0),
(2, 5, '男士休闲 T 恤', '纯棉材质，舒适透气', 99.00, 200, 3000, 1, 0),
(2, 6, '女士连衣裙', '时尚优雅，适合夏季穿着', 199.00, 150, 2000, 1, 0);

-- =====================================================
-- 插入测试商品图片
-- =====================================================
INSERT INTO product_images (product_id, image_url, sort, is_main) VALUES
(1, 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=smartphone%20product%20image&image_size=square', 1, 1),
(1, 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=smartphone%20side%20view&image_size=square', 2, 0),
(2, 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=premium%20smartphone&image_size=square', 1, 1),
(3, 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=laptop%20computer&image_size=square', 1, 1),
(4, 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=men%20t-shirt&image_size=square', 1, 1),
(5, 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=women%20dress&image_size=square', 1, 1);

-- =====================================================
-- 插入测试商品规格
-- =====================================================
-- 智能手机 X1 的规格
INSERT INTO product_skus (product_id, sku_name, price, stock, sales) VALUES
(1, '黑色 128GB', 3999.00, 30, 400),
(1, '白色 128GB', 3999.00, 40, 500),
(1, '黑色 256GB', 4299.00, 30, 300);

-- 智能手机 Pro 的规格
INSERT INTO product_skus (product_id, sku_name, price, stock, sales) VALUES
(2, '黑色 256GB', 5999.00, 20, 300),
(2, '银色 256GB', 5999.00, 15, 250),
(2, '黑色 512GB', 6999.00, 15, 250);

-- =====================================================
-- 插入测试商品属性
-- =====================================================
-- 智能手机 X1 的属性
INSERT INTO product_attributes (product_id, attribute_name) VALUES
(1, '颜色'),
(1, '内存');

-- 属性值
INSERT INTO product_attribute_values (attribute_id, value_name) VALUES
(1, '黑色'),
(1, '白色'),
(2, '128GB'),
(2, '256GB');

-- 规格与属性关联
INSERT INTO product_sku_attributes (sku_id, attribute_value_id) VALUES
(1, 1), (1, 3),  -- 黑色 128GB
(2, 2), (2, 3),  -- 白色 128GB
(3, 1), (3, 4);  -- 黑色 256GB

-- =====================================================
-- 插入测试购物车
-- =====================================================
INSERT INTO carts (user_id) VALUES
(2),
(3);

-- =====================================================
-- 插入测试购物车商品
-- =====================================================
INSERT INTO cart_items (cart_id, product_id, sku_id, quantity, price) VALUES
(1, 1, 1, 1, 3999.00),
(1, 4, NULL, 2, 99.00),
(2, 5, NULL, 1, 199.00);

-- =====================================================
-- 插入测试订单
-- =====================================================
INSERT INTO orders (order_no, user_id, address_id, store_id, total_amount, actual_amount, status, payment_method, payment_time) VALUES
('ORD202603170001', 2, 1, 1, 4098.00, 4098.00, 'completed', 'alipay', '2026-03-15 10:30:00'),
('ORD202603170002', 3, 3, 2, 199.00, 199.00, 'shipping', 'wechat', '2026-03-16 14:20:00');

-- =====================================================
-- 插入测试订单商品
-- =====================================================
INSERT INTO order_items (order_id, product_id, sku_id, product_name, sku_name, price, quantity) VALUES
(1, 1, 1, '智能手机 X1', '黑色 128GB', 3999.00, 1),
(1, 4, NULL, '男士休闲 T 恤', NULL, 99.00, 2),
(2, 5, NULL, '女士连衣裙', NULL, 199.00, 1);

-- =====================================================
-- 插入测试物流信息
-- =====================================================
INSERT INTO shipments (order_id, shipping_company, tracking_number, status) VALUES
(2, '顺丰速运', 'SF1234567890', 'shipping');

-- =====================================================
-- 插入测试商品评论
-- =====================================================
INSERT INTO comments (user_id, product_id, order_item_id, content, rating) VALUES
(2, 1, 1, '手机很好用，性能强劲，拍照效果也很棒！', 5),
(2, 4, 2, 'T 恤质量不错，穿着舒适，性价比高！', 4);

-- =====================================================
-- 插入测试收藏
-- =====================================================
INSERT INTO favorites (user_id, product_id) VALUES
(2, 2),
(2, 3),
(3, 5);

-- =====================================================
-- 插入测试博客文章
-- =====================================================
INSERT INTO blogs (title, content, author_id, category_id, tags, view_count, comment_count, status) VALUES
('如何选购智能手机', '随着科技的发展，智能手机已经成为我们生活中不可或缺的一部分...', 1, 2, '购物，教程', 1500, 10, 1),
('2026 年笔记本电脑推荐', '本文将为大家推荐几款 2026 年值得购买的笔记本电脑...', 1, 1, 'Java，教程', 2000, 15, 1),
('夏季穿搭指南', '夏天到了，如何穿搭才能既舒适又时尚呢？...', 1, 3, '技巧', 800, 5, 1);

-- =====================================================
-- 插入测试博客评论
-- =====================================================
INSERT INTO blog_comments (blog_id, user_id, content) VALUES
(1, 2, '文章很有用，学到了很多！'),
(1, 3, '感谢分享，正打算买手机呢'),
(2, 2, '笔记本推荐很全面，已种草一款');

-- =====================================================
-- 插入测试秒杀活动
-- =====================================================
INSERT INTO seckill_activities (name, start_time, end_time, status) VALUES
('315 数码狂欢节', '2026-03-15 00:00:00', '2026-03-17 23:59:59', 1),
('春季新品秒杀', '2026-03-18 00:00:00', '2026-03-20 23:59:59', 0);

-- =====================================================
-- 插入测试秒杀商品
-- =====================================================
INSERT INTO seckill_products (activity_id, product_id, seckill_price, stock, sold, status) VALUES
(1, 1, 3599.00, 20, 15, 1),
(1, 2, 5399.00, 10, 8, 1),
(1, 3, 4499.00, 15, 10, 1);

-- =====================================================
-- 插入测试统计记录
-- =====================================================
INSERT INTO stats_records (stats_type, stats_date, stats_value) VALUES
('sales', '2026-03-15', 15000.00),
('sales', '2026-03-16', 18000.00),
('user', '2026-03-15', 250),
('user', '2026-03-16', 265),
('product', '2026-03-15', 500),
('product', '2026-03-16', 520);

-- =====================================================
-- 显示完成信息
-- =====================================================
SELECT 'Test data inserted successfully!' AS result;
SELECT 'Total users: ' || COUNT(*) AS info FROM users;
SELECT 'Total products: ' || COUNT(*) AS info FROM products;
SELECT 'Total stores: ' || COUNT(*) AS info FROM stores;
SELECT 'Total orders: ' || COUNT(*) AS info FROM orders;
