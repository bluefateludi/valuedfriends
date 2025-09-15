-- 检查数据库中可能存在格式不正确的标签数据
-- 用于诊断用户推荐功能中的JSON解析问题

USE valuesfriends;

-- 1. 查找所有非空但可能格式不正确的标签
SELECT 
    id,
    username,
    tags,
    CASE 
        WHEN tags IS NULL THEN '标签为NULL'
        WHEN tags = '' THEN '标签为空字符串'
        WHEN tags = '[]' THEN '标签为空数组'
        WHEN tags NOT LIKE '[%' THEN '不以[开头'
        WHEN tags NOT LIKE '%]' THEN '不以]结尾'
        WHEN tags LIKE '%\'%' THEN '包含单引号'
        WHEN tags LIKE '%,%]' THEN '末尾有多余逗号'
        WHEN tags NOT LIKE '%"%' THEN '缺少双引号'
        ELSE '格式可能正确'
    END AS tag_status
FROM user 
WHERE userStatus = 0 AND isDelete = 0
ORDER BY 
    CASE 
        WHEN tags IS NULL THEN 1
        WHEN tags = '' THEN 2
        WHEN tags = '[]' THEN 3
        WHEN tags NOT LIKE '[%' THEN 4
        WHEN tags NOT LIKE '%]' THEN 5
        WHEN tags LIKE '%\'%' THEN 6
        WHEN tags LIKE '%,%]' THEN 7
        WHEN tags NOT LIKE '%"%' THEN 8
        ELSE 9
    END;

-- 2. 统计各种标签格式的数量
SELECT 
    CASE 
        WHEN tags IS NULL THEN '标签为NULL'
        WHEN tags = '' THEN '标签为空字符串'
        WHEN tags = '[]' THEN '标签为空数组'
        WHEN tags NOT LIKE '[%' THEN '不以[开头'
        WHEN tags NOT LIKE '%]' THEN '不以]结尾'
        WHEN tags LIKE '%\'%' THEN '包含单引号'
        WHEN tags LIKE '%,%]' THEN '末尾有多余逗号'
        WHEN tags NOT LIKE '%"%' THEN '缺少双引号'
        ELSE '格式可能正确'
    END AS tag_status,
    COUNT(*) as count
FROM user 
WHERE userStatus = 0 AND isDelete = 0
GROUP BY 
    CASE 
        WHEN tags IS NULL THEN '标签为NULL'
        WHEN tags = '' THEN '标签为空字符串'
        WHEN tags = '[]' THEN '标签为空数组'
        WHEN tags NOT LIKE '[%' THEN '不以[开头'
        WHEN tags NOT LIKE '%]' THEN '不以]结尾'
        WHEN tags LIKE '%\'%' THEN '包含单引号'
        WHEN tags LIKE '%,%]' THEN '末尾有多余逗号'
        WHEN tags NOT LIKE '%"%' THEN '缺少双引号'
        ELSE '格式可能正确'
    END
ORDER BY count DESC;

-- 3. 查找特定用户ID的标签信息（用于调试）
SELECT 
    id,
    username,
    tags,
    LENGTH(tags) as tag_length,
    CHAR_LENGTH(tags) as tag_char_length
FROM user 
WHERE id = 196311857132285133;

-- 4. 查找所有有标签的用户（用于推荐算法的候选用户）
SELECT 
    COUNT(*) as total_users_with_tags
FROM user 
WHERE userStatus = 0 
    AND isDelete = 0 
    AND tags IS NOT NULL 
    AND tags != '' 
    AND tags != '[]';

-- 5. 显示一些示例标签数据
SELECT 
    id,
    username,
    tags
FROM user 
WHERE userStatus = 0 
    AND isDelete = 0 
    AND tags IS NOT NULL 
    AND tags != '' 
    AND tags != '[]'
LIMIT 10;

-- 6. 查找可能导致推荐结果为空的问题
SELECT 
    '总用户数' as metric,
    COUNT(*) as value
FROM user 
WHERE userStatus = 0 AND isDelete = 0

UNION ALL

SELECT 
    '有标签的用户数' as metric,
    COUNT(*) as value
FROM user 
WHERE userStatus = 0 
    AND isDelete = 0 
    AND tags IS NOT NULL 
    AND tags != '' 
    AND tags != '[]'

UNION ALL

SELECT 
    '格式正确的标签用户数' as metric,
    COUNT(*) as value
FROM user 
WHERE userStatus = 0 
    AND isDelete = 0 
    AND tags LIKE '[%'
    AND tags LIKE '%]'
    AND tags LIKE '%"%';