-- =====================================================
-- 店铺服务 - 初始化数据
-- =====================================================

USE store_db;

-- =====================================================
-- 插入店铺数据
-- =====================================================
INSERT INTO stores (id, store_name, owner_id, description, status, follow_count, rating, rating_count) 
VALUES 
(1, '官方旗舰店', 1, '官方自营店铺，品质保证', 1, 150, 4.9, 100),
(2, '数码专营店', 1, '专业数码产品销售', 1, 80, 4.8, 50);

-- =====================================================
-- 插入店铺关注数据
-- =====================================================
INSERT INTO store_follows (store_id, user_id) 
VALUES 
(1, 1),
(1, 2),
(2, 1);

-- =====================================================
-- 插入店铺评分数据
-- =====================================================
INSERT INTO store_ratings (store_id, user_id, rating, comment) 
VALUES 
(1, 1, 5, '非常好的店铺，物流快速！'),
(1, 2, 5, '品质保证，值得信赖！'),
(2, 1, 4, '产品不错，价格合理。');

-- =====================================================
-- 更新店铺关注数和评分数
-- =====================================================
UPDATE stores SET follow_count = (SELECT COUNT(*) FROM store_follows WHERE store_id = stores.id);
UPDATE stores SET rating_count = (SELECT COUNT(*) FROM store_ratings WHERE store_id = stores.id);
UPDATE stores s SET s.rating = (
    SELECT AVG(rating) FROM store_ratings WHERE store_id = s.id
) WHERE rating_count > 0;

SELECT 'Store service init data inserted successfully!' AS result;
