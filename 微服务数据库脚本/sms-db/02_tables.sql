-- =====================================================
-- 短信服务 - 数据表结构
-- 包含表：sms_templates, sms_records
-- =====================================================

USE sms_db;

-- =====================================================
-- 创建 sms_templates 表
-- =====================================================
DROP TABLE IF EXISTS sms_templates;

CREATE TABLE sms_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模板 ID',
    template_code VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编码',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    content VARCHAR(500) NOT NULL COMMENT '模板内容',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信模板表';

-- =====================================================
-- 创建 sms_records 表
-- =====================================================
DROP TABLE IF EXISTS sms_records;

CREATE TABLE sms_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录 ID',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    template_code VARCHAR(50) NOT NULL COMMENT '模板编码',
    content VARCHAR(500) NOT NULL COMMENT '发送内容',
    status TINYINT NOT NULL COMMENT '发送状态（0-失败，1-成功）',
    error_message VARCHAR(255) COMMENT '错误信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_phone (phone),
    INDEX idx_create_time (create_time),
    INDEX idx_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信发送记录表';

SELECT 'SMS service tables created successfully!' AS result;
