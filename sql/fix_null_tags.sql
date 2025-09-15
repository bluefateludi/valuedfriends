-- 修复用户表中tags字段为NULL的问题
-- 针对测试一和测试六的数据修复脚本

USE valuesfriends;

-- 1. 查看当前存在NULL tags的用户记录
SELECT 
    id,
    username,
    userAccount,
    tags,
    CASE 
        WHEN tags IS NULL THEN '标签为NULL'
        WHEN tags = '' THEN '标签为空字符串'
        ELSE '标签正常'
    END AS tag_status
FROM user 
WHERE tags IS NULL OR tags = ''
ORDER BY id;

-- 2. 修复测试一（id: 196311857132285133）的tags字段
-- 根据用户名"张三"推测应该是Java后端开发相关标签
UPDATE user 
SET tags = '["Java", "后端开发"]'
WHERE id = 196311857132285133 AND tags IS NULL;

-- 3. 查找并修复其他可能的NULL tags记录
-- 为所有tags为NULL的用户设置默认标签
UPDATE user 
SET tags = '["编程", "技术学习"]'
WHERE tags IS NULL AND userStatus = 0 AND isDelete = 0;

-- 4. 修复空字符串的tags字段
UPDATE user 
SET tags = '["新用户", "待完善"]'
WHERE tags = '' AND userStatus = 0 AND isDelete = 0;

-- 5. 验证修复结果
SELECT 
    id,
    username,
    userAccount,
    tags,
    CASE 
        WHEN tags IS NULL THEN '标签为NULL'
        WHEN tags = '' THEN '标签为空字符串'
        WHEN tags = '[]' THEN '标签为空数组'
        WHEN tags LIKE '[%' AND tags LIKE '%]' AND tags LIKE '%"%' THEN '格式正确'
        ELSE '格式可能有问题'
    END AS tag_status
FROM user 
WHERE userStatus = 0 AND isDelete = 0
ORDER BY 
    CASE 
        WHEN tags IS NULL THEN 1
        WHEN tags = '' THEN 2
        WHEN tags = '[]' THEN 3
        ELSE 4
    END, id
LIMIT 20;

-- 6. 统计修复后的标签状态
SELECT 
    CASE 
        WHEN tags IS NULL THEN '标签为NULL'
        WHEN tags = '' THEN '标签为空字符串'
        WHEN tags = '[]' THEN '标签为空数组'
        WHEN tags LIKE '[%' AND tags LIKE '%]' AND tags LIKE '%"%' THEN '格式正确'
        ELSE '格式可能有问题'
    END AS tag_status,
    COUNT(*) as count
FROM user 
WHERE userStatus = 0 AND isDelete = 0
GROUP BY 
    CASE 
        WHEN tags IS NULL THEN '标签为NULL'
        WHEN tags = '' THEN '标签为空字符串'
        WHEN tags = '[]' THEN '标签为空数组'
        WHEN tags LIKE '[%' AND tags LIKE '%]' AND tags LIKE '%"%' THEN '格式正确'
        ELSE '格式可能有问题'
    END
ORDER BY count DESC;

-- 7. 检查特定用户的修复结果
SELECT 
    id,
    username,
    userAccount,
    tags
FROM user 
WHERE id IN (196311857132285133, 196318767013799362)
OR username IN ('张三', '孙八');