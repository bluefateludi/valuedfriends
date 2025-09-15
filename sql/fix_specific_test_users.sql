-- 修复特定测试用户的tags字段问题
-- 专门针对测试一和测试六的精确修复

USE valuesfriends;

-- 查看问题用户的当前状态
SELECT 
    id,
    username,
    userAccount,
    tags,
    planetCode
FROM user 
WHERE userAccount IN ('zhangsan001', 'sunba006')
OR planetCode IN ('YX001', 'YX006')
OR id IN (196311857132285133, 196318767013799362);

-- 修复测试一：张三 (zhangsan001, YX001)
-- 根据原始测试数据，应该是 ["Java", "后端开发"]
UPDATE user 
SET tags = '["Java", "后端开发"]'
WHERE (userAccount = 'zhangsan001' OR planetCode = 'YX001' OR id = 196311857132285133)
  AND (tags IS NULL OR tags = '');

-- 修复测试六：孙八 (sunba006, YX006)
-- 根据原始测试数据，应该是 ["测试", "自动化测试"]
UPDATE user 
SET tags = '["测试", "自动化测试"]'
WHERE (userAccount = 'sunba006' OR planetCode = 'YX006')
  AND (tags IS NULL OR tags = '');

-- 验证修复结果
SELECT 
    id,
    username,
    userAccount,
    planetCode,
    tags,
    CASE 
        WHEN tags IS NULL THEN '❌ 标签为NULL'
        WHEN tags = '' THEN '❌ 标签为空字符串'
        WHEN tags = '[]' THEN '⚠️ 标签为空数组'
        WHEN tags LIKE '[%' AND tags LIKE '%]' AND tags LIKE '%"%' THEN '✅ 格式正确'
        ELSE '❌ 格式有问题'
    END AS tag_status
FROM user 
WHERE userAccount IN ('zhangsan001', 'sunba006')
OR planetCode IN ('YX001', 'YX006')
OR id IN (196311857132285133, 196318767013799362)
ORDER BY userAccount;

-- 检查所有测试用户的标签状态
SELECT 
    planetCode,
    username,
    userAccount,
    tags,
    CASE 
        WHEN tags IS NULL THEN '❌ NULL'
        WHEN tags = '' THEN '❌ 空串'
        WHEN tags = '[]' THEN '⚠️ 空数组'
        WHEN tags LIKE '[%' AND tags LIKE '%]' AND tags LIKE '%"%' THEN '✅ 正常'
        ELSE '❌ 异常'
    END AS status
FROM user 
WHERE planetCode LIKE 'YX%'
ORDER BY 
    CASE 
        WHEN tags IS NULL THEN 1
        WHEN tags = '' THEN 2
        WHEN tags = '[]' THEN 3
        ELSE 4
    END,
    CAST(SUBSTRING(planetCode, 3) AS UNSIGNED);

-- 统计修复后的整体情况
SELECT 
    '修复前问题用户数' as description,
    COUNT(*) as count
FROM user 
WHERE (tags IS NULL OR tags = '') 
  AND userStatus = 0 
  AND isDelete = 0

UNION ALL

SELECT 
    '总测试用户数' as description,
    COUNT(*) as count
FROM user 
WHERE planetCode LIKE 'YX%'
  AND userStatus = 0 
  AND isDelete = 0

UNION ALL

SELECT 
    '标签格式正确的用户数' as description,
    COUNT(*) as count
FROM user 
WHERE planetCode LIKE 'YX%'
  AND tags LIKE '[%' 
  AND tags LIKE '%]' 
  AND tags LIKE '%"%'
  AND userStatus = 0 
  AND isDelete = 0;