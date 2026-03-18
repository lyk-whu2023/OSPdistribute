-- =====================================================
-- 传统分布式购物平台 - 数据库验证脚本
-- 用于检查数据库、表和默认数据是否正确创建
-- 创建时间：2026-03-17
-- =====================================================

USE shopping_platform_db;

-- =====================================================
-- 1. 检查所有表是否存在
-- =====================================================
SELECT '=== 检查所有表 ===' AS section;

SELECT 
    TABLE_NAME AS '表名',
    TABLE_COMMENT AS '表描述'
FROM 
    information_schema.TABLES 
WHERE 
    TABLE_SCHEMA = 'shopping_platform_db'
ORDER BY 
    TABLE_NAME;

-- =====================================================
-- 2. 检查表总数
-- =====================================================
SELECT '=== 表总数统计 ===' AS section;

SELECT 
    COUNT(*) AS '表总数'
FROM 
    information_schema.TABLES 
WHERE 
    TABLE_SCHEMA = 'shopping_platform_db';

-- =====================================================
-- 3. 检查各模块表数量
-- =====================================================
SELECT '=== 各模块表数量 ===' AS section;

SELECT 
    CASE 
        WHEN TABLE_NAME IN ('users', 'addresses', 'sms_templates', 'sms_records') THEN '用户与认证'
        WHEN TABLE_NAME IN ('categories', 'products', 'product_images', 'product_skus', 
                           'product_attributes', 'product_attribute_values', 'product_sku_attributes',
                           'seckill_products', 'seckill_activities') THEN '商品与库存'
        WHEN TABLE_NAME IN ('carts', 'cart_items', 'orders', 'order_items', 'seckill_orders', 'payments') THEN '订单与支付'
        WHEN TABLE_NAME IN ('shipments', 'comments', 'favorites') THEN '物流与评论'
        WHEN TABLE_NAME IN ('blogs', 'blog_categories', 'blog_tags', 'blog_comments', 'stats_records') THEN '内容与统计'
        WHEN TABLE_NAME IN ('stores', 'store_follows', 'store_ratings') THEN '店铺'
        ELSE '其他'
    END AS '模块',
    COUNT(*) AS '表数量'
FROM 
    information_schema.TABLES 
WHERE 
    TABLE_SCHEMA = 'shopping_platform_db'
GROUP BY 
    CASE 
        WHEN TABLE_NAME IN ('users', 'addresses', 'sms_templates', 'sms_records') THEN '用户与认证'
        WHEN TABLE_NAME IN ('categories', 'products', 'product_images', 'product_skus', 
                           'product_attributes', 'product_attribute_values', 'product_sku_attributes',
                           'seckill_products', 'seckill_activities') THEN '商品与库存'
        WHEN TABLE_NAME IN ('carts', 'cart_items', 'orders', 'order_items', 'seckill_orders', 'payments') THEN '订单与支付'
        WHEN TABLE_NAME IN ('shipments', 'comments', 'favorites') THEN '物流与评论'
        WHEN TABLE_NAME IN ('blogs', 'blog_categories', 'blog_tags', 'blog_comments', 'stats_records') THEN '内容与统计'
        WHEN TABLE_NAME IN ('stores', 'store_follows', 'store_ratings') THEN '店铺'
        ELSE '其他'
    END
ORDER BY 
    COUNT(*) DESC;

-- =====================================================
-- 4. 检查默认用户数据
-- =====================================================
SELECT '=== 默认用户数据 ===' AS section;

SELECT 
    id,
    username,
    nickname,
    role,
    status,
    register_time
FROM 
    users
WHERE 
    role = 'admin';

-- =====================================================
-- 5. 检查商品分类
-- =====================================================
SELECT '=== 商品分类 ===' AS section;

SELECT 
    id,
    name,
    parent_id,
    level,
    status
FROM 
    categories
ORDER BY 
    level, sort;

-- =====================================================
-- 6. 检查店铺数据
-- =====================================================
SELECT '=== 店铺数据 ===' AS section;

SELECT 
    id,
    store_name,
    owner_id,
    status,
    rating,
    follow_count,
    create_time
FROM 
    stores;

-- =====================================================
-- 7. 检查博客分类和标签
-- =====================================================
SELECT '=== 博客分类 ===' AS section;

SELECT 
    id,
    name,
    parent_id,
    sort
FROM 
    blog_categories
ORDER BY 
    sort;

SELECT '=== 博客标签 ===' AS section;

SELECT 
    id,
    name
FROM 
    blog_tags;

-- =====================================================
-- 8. 检查短信模板
-- =====================================================
SELECT '=== 短信模板 ===' AS section;

SELECT 
    id,
    template_code,
    template_name,
    status
FROM 
    sms_templates;

-- =====================================================
-- 9. 统计信息汇总
-- =====================================================
SELECT '=== 数据统计汇总 ===' AS section;

SELECT 'users' AS '表名', COUNT(*) AS '记录数' FROM users
UNION ALL
SELECT 'categories', COUNT(*) FROM categories
UNION ALL
SELECT 'stores', COUNT(*) FROM stores
UNION ALL
SELECT 'sms_templates', COUNT(*) FROM sms_templates
UNION ALL
SELECT 'blog_categories', COUNT(*) FROM blog_categories
UNION ALL
SELECT 'blog_tags', COUNT(*) FROM blog_tags;

-- =====================================================
-- 10. 显示完成信息
-- =====================================================
SELECT '========================================' AS '';
SELECT '数据库验证完成！' AS result;
SELECT '========================================' AS '';
