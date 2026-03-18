-- =====================================================
-- 用户服务 - 初始化数据
-- =====================================================

USE user_db;

-- =====================================================
-- 插入默认管理员账户
-- 密码为加密后的 123456 (使用 BCrypt 加密)
-- =====================================================
INSERT INTO users (username, password, phone, email, nickname, role, status) 
VALUES 
('admin', '$2a$10$XoLvF5C2dz9.7JHK8sN.TOxl9yYp4CqKQ8MmqPHAKQYYQZQxZP8W', '13800138000', 'admin@shopping.com', '系统管理员', 'admin', 1),
('test_user', '$2a$10$XoLvF5C2dz9.7JHK8sN.TOxl9yYp4CqKQ8MmqPHAKQYYQZQxZP8W', '13900139000', 'test@shopping.com', '测试用户', 'user', 1);

-- =====================================================
-- 插入测试地址数据
-- =====================================================
INSERT INTO addresses (user_id, name, phone, province, city, district, detail, is_default) 
VALUES 
(1, '系统管理员', '13800138000', '北京市', '北京市', '朝阳区', '测试地址 1 号', 1),
(2, '测试用户', '13900139000', '上海市', '上海市', '浦东新区', '测试地址 2 号', 1);

SELECT 'User service init data inserted successfully!' AS result;
