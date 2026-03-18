-- =====================================================
-- 评论服务 - 初始化数据
-- =====================================================

USE comment_db;

-- =====================================================
-- 插入评论数据
-- =====================================================
INSERT INTO comments (user_id, product_id, order_item_id, content, rating, image_urls) 
VALUES 
(1, 1, 1, '非常好用，性能强劲，拍照效果出色！', 5, '/images/comments/img1.jpg,/images/comments/img2.jpg'),
(1, 3, 2, '衬衫质量不错，穿着舒适。', 4, NULL),
(2, 2, 3, '电脑运行流畅，适合专业工作。', 5, '/images/comments/img3.jpg');

-- =====================================================
-- 插入收藏数据
-- =====================================================
INSERT INTO favorites (user_id, product_id) 
VALUES 
(1, 2),
(1, 4),
(2, 1),
(2, 3);

SELECT 'Comment service init data inserted successfully!' AS result;
