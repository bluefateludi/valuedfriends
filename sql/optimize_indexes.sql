-- 用户表索引优化
-- 为匹配功能优化的索引

-- 1. 用户状态和标签复合索引（用于快速筛选有效用户）
CREATE INDEX idx_user_status_tags ON user (userStatus, tags(100));

-- 2. 创建时间索引（用于按时间排序和分页）
CREATE INDEX idx_user_create_time ON user (createTime);

-- 3. 用户状态索引（用于快速筛选正常用户）
CREATE INDEX idx_user_status ON user (userStatus);

-- 4. 标签字段索引（用于标签相关查询）
CREATE INDEX idx_user_tags ON user (tags(200));

-- 5. ID和状态复合索引（用于排除当前用户且筛选正常用户）
CREATE INDEX idx_user_id_status ON user (id, userStatus);

-- 6. 性别索引（如果需要按性别筛选）
CREATE INDEX idx_user_gender ON user (gender);

-- 7. 用户角色索引（如果需要按角色筛选）
CREATE INDEX idx_user_role ON user (userRole);

-- 匹配配置表索引
-- 配置键唯一索引已在建表时创建，这里添加状态相关索引
CREATE INDEX idx_match_config_active ON match_config (is_active);

-- 查看当前索引状态
-- SHOW INDEX FROM user;
-- SHOW INDEX FROM match_config;

-- 分析表统计信息（建议在数据导入后执行）
-- ANALYZE TABLE user;
-- ANALYZE TABLE match_config;

-- 优化建议注释：
-- 1. 定期执行 OPTIMIZE TABLE user; 来整理表碎片
-- 2. 监控慢查询日志，根据实际查询模式调整索引
-- 3. 考虑使用分区表，按创建时间或用户ID范围分区
-- 4. 对于大量数据，可以考虑使用覆盖索引减少回表查询
-- 5. 标签字段如果经常进行全文搜索，可以考虑使用全文索引

-- 全文索引示例（可选，适用于标签内容搜索）
-- ALTER TABLE user ADD FULLTEXT(tags);

-- 如果需要删除某些索引，使用以下语句：
-- DROP INDEX idx_user_status_tags ON user;
-- DROP INDEX idx_user_create_time ON user;
-- DROP INDEX idx_user_status ON user;
-- DROP INDEX idx_user_tags ON user;
-- DROP INDEX idx_user_id_status ON user;
-- DROP INDEX idx_user_gender ON user;
-- DROP INDEX idx_user_role ON user;
-- DROP INDEX idx_match_config_active ON match_config;