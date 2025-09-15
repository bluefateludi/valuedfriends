# SQL测试数据修复说明

## 问题描述

根据测试结果，发现数据库中存在以下问题：
- **测试一**（用户ID: 196311857132285133，张三）：tags字段为NULL
- **测试六**（用户账号: sunba006，孙八）：tags字段为NULL
- 其他用户的tags字段格式正常

## 修复方案

### 1. 通用修复脚本
**文件**: `fix_null_tags.sql`

**功能**:
- 检查所有tags为NULL或空字符串的用户
- 为NULL tags设置默认值
- 验证修复结果
- 提供统计信息

### 2. 精确修复脚本
**文件**: `fix_specific_test_users.sql`

**功能**:
- 专门修复测试一和测试六的用户数据
- 根据原始测试数据恢复正确的标签
- 提供详细的验证和状态检查

## 使用步骤

### 步骤1: 执行精确修复（推荐）
```sql
-- 在MySQL客户端中执行
source d:/Program code/java/valuedfriends/sql/fix_specific_test_users.sql;
```

### 步骤2: 验证修复结果
执行脚本后，查看输出结果：
- ✅ 格式正确：表示tags字段已正确修复
- ❌ 标签为NULL：表示仍需修复
- ⚠️ 标签为空数组：表示用户没有设置标签

### 步骤3: 如有需要，执行通用修复
如果还有其他用户存在类似问题：
```sql
source d:/Program code/java/valuedfriends/sql/fix_null_tags.sql;
```

## 修复内容

### 测试一（张三）
- **原始标签**: `["Java", "后端开发"]`
- **问题**: tags字段为NULL
- **修复**: 恢复为原始标签值

### 测试六（孙八）
- **原始标签**: `["测试", "自动化测试"]`
- **问题**: tags字段为NULL
- **修复**: 恢复为原始标签值

## 验证方法

### 1. 检查特定用户
```sql
SELECT id, username, userAccount, tags 
FROM user 
WHERE userAccount IN ('zhangsan001', 'sunba006');
```

### 2. 检查所有测试用户
```sql
SELECT planetCode, username, tags,
       CASE 
           WHEN tags IS NULL THEN '❌ NULL'
           WHEN tags LIKE '[%' AND tags LIKE '%]' THEN '✅ 正常'
           ELSE '❌ 异常'
       END AS status
FROM user 
WHERE planetCode LIKE 'YX%'
ORDER BY CAST(SUBSTRING(planetCode, 3) AS UNSIGNED);
```

## 注意事项

1. **备份数据**: 执行修复脚本前建议备份数据库
2. **测试环境**: 建议先在测试环境中验证脚本效果
3. **权限检查**: 确保数据库用户有UPDATE权限
4. **字符编码**: 确保数据库连接使用UTF-8编码

## 预期结果

修复完成后，所有测试用户应该都有正确格式的JSON标签：
- 测试一：`["Java", "后端开发"]`
- 测试六：`["测试", "自动化测试"]`
- 其他用户：保持原有正确格式的标签

这样可以确保用户推荐功能能够正常工作，基于标签相似度进行用户匹配。