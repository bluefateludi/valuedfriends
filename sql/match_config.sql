use valuesfriends ;
-- 匹配配置表
CREATE TABLE match_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value VARCHAR(500) NOT NULL COMMENT '配置值',
    description VARCHAR(200) COMMENT '配置描述',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='匹配配置表';

-- 插入默认配置
INSERT INTO match_config (config_key, config_value, description) VALUES
('batch_size', '20', '每批次查询用户数量'),
('max_batches', '10', '最大批次数量'),
('min_similarity', '0.1', '最小相似度阈值'),
('cache_expire_minutes', '30', '缓存过期时间(分钟)'),
('enable_batch_rotation', 'true', '是否启用分页轮换'),
('rotation_offset_increment', '5', '轮换偏移量增量'),
('max_recommendations', '50', '最大推荐用户数量');