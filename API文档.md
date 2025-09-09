# ValuedFriends API 接口文档

## 1. 概述

本文档描述了 ValuedFriends 项目的所有 REST API 接口，包括用户管理、帖子管理、文件上传、点赞收藏等功能模块。

### 1.1 基础信息

- **基础URL**: `http://localhost:8081`
- **数据格式**: JSON
- **字符编码**: UTF-8
- **认证方式**: Session 认证

### 1.2 通用响应格式

所有接口都使用统一的响应格式：

```json
{
  "code": 0,
  "data": {},
  "message": "ok"
}
```

**响应字段说明：**
- `code`: 状态码，0表示成功，非0表示失败
- `data`: 响应数据，具体结构根据接口而定
- `message`: 响应消息

### 1.3 分页响应格式

分页接口的响应数据结构：

```json
{
  "code": 0,
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  },
  "message": "ok"
}
```

**分页字段说明：**
- `records`: 当前页数据列表
- `total`: 总记录数
- `size`: 每页大小
- `current`: 当前页码
- `pages`: 总页数

## 2. 错误码定义

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 0 | ok | 成功 |
| 40000 | 请求参数错误 | 请求参数格式错误或缺少必要参数 |
| 40100 | 未登录 | 用户未登录 |
| 40101 | 无权限 | 用户权限不足 |
| 40300 | 禁止访问 | 访问被禁止 |
| 40400 | 请求数据不存在 | 请求的资源不存在 |
| 50000 | 系统内部异常 | 服务器内部错误 |
| 50001 | 操作失败 | 数据库操作失败 |

## 3. 用户管理接口

### 3.1 用户注册

**接口描述**: 用户注册新账号

- **URL**: `/user/register`
- **请求方法**: POST
- **是否需要登录**: 否

**请求参数**:
```json
{
  "userAccount": "testuser",
  "userPassword": "12345678",
  "checkPassword": "12345678"
}
```

**参数说明**:
- `userAccount` (string, 必填): 用户账号，长度4-16位
- `userPassword` (string, 必填): 用户密码，长度不少于8位
- `checkPassword` (string, 必填): 确认密码，必须与密码一致

**响应示例**:
```json
{
  "code": 0,
  "data": 1001,
  "message": "ok"
}
```

**响应数据**: 新用户的ID

### 3.2 用户登录

**接口描述**: 用户登录系统

- **URL**: `/user/login`
- **请求方法**: POST
- **是否需要登录**: 否

**请求参数**:
```json
{
  "userAccount": "testuser",
  "userPassword": "12345678"
}
```

**参数说明**:
- `userAccount` (string, 必填): 用户账号
- `userPassword` (string, 必填): 用户密码

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "id": 1001,
    "userName": "测试用户",
    "userAvatar": "https://example.com/avatar.jpg",
    "userRole": "user",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  },
  "message": "ok"
}
```

### 3.3 用户注销

**接口描述**: 用户注销登录

- **URL**: `/user/logout`
- **请求方法**: POST
- **是否需要登录**: 是

**请求参数**: 无

**响应示例**:
```json
{
  "code": 0,
  "data": true,
  "message": "ok"
}
```

### 3.4 获取当前登录用户信息

**接口描述**: 获取当前登录用户的详细信息

- **URL**: `/user/get/login`
- **请求方法**: GET
- **是否需要登录**: 是

**请求参数**: 无

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "id": 1001,
    "userName": "测试用户",
    "userAvatar": "https://example.com/avatar.jpg",
    "userRole": "user",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  },
  "message": "ok"
}
```

### 3.5 创建用户（管理员）

**接口描述**: 管理员创建新用户

- **URL**: `/user/add`
- **请求方法**: POST
- **是否需要登录**: 是
- **权限要求**: 管理员

**请求参数**:
```json
{
  "userAccount": "newuser",
  "username": "新用户",
  "avatarUrl": "https://example.com/avatar.jpg",
  "gender": 1,
  "phone": "13800138000",
  "email": "user@example.com",
  "userRole": "user"
}
```

**参数说明**:
- `userAccount` (string, 必填): 用户账号
- `username` (string, 可选): 用户昵称
- `avatarUrl` (string, 可选): 头像URL
- `gender` (integer, 可选): 性别，0-女，1-男
- `phone` (string, 可选): 手机号
- `email` (string, 可选): 邮箱
- `userRole` (string, 可选): 用户角色

**响应示例**:
```json
{
  "code": 0,
  "data": 1002,
  "message": "ok"
}
```

### 3.6 删除用户（管理员）

**接口描述**: 管理员删除用户

- **URL**: `/user/delete`
- **请求方法**: POST
- **是否需要登录**: 是
- **权限要求**: 管理员

**请求参数**:
```json
{
  "id": 1002
}
```

**参数说明**:
- `id` (long, 必填): 要删除的用户ID

**响应示例**:
```json
{
  "code": 0,
  "data": true,
  "message": "ok"
}
```

### 3.7 更新用户信息

**接口描述**: 更新用户信息（用户只能更新自己的信息，管理员可以更新任何用户）

- **URL**: `/user/update`
- **请求方法**: POST
- **是否需要登录**: 是

**请求参数**:
```json
{
  "id": 1001,
  "username": "新昵称",
  "avatarUrl": "https://example.com/new-avatar.jpg",
  "gender": 0,
  "phone": "13900139000",
  "email": "newemail@example.com"
}
```

**参数说明**:
- `id` (long, 必填): 用户ID
- `username` (string, 可选): 用户昵称
- `avatarUrl` (string, 可选): 头像URL
- `gender` (integer, 可选): 性别
- `phone` (string, 可选): 手机号
- `email` (string, 可选): 邮箱

**响应示例**:
```json
{
  "code": 0,
  "data": true,
  "message": "ok"
}
```

### 3.8 根据ID获取用户信息（管理员）

**接口描述**: 管理员根据ID获取用户详细信息

- **URL**: `/user/get`
- **请求方法**: GET
- **是否需要登录**: 是
- **权限要求**: 管理员

**请求参数**:
- `id` (long, 必填): 用户ID，通过URL参数传递

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "id": 1001,
    "userAccount": "testuser",
    "username": "测试用户",
    "avatarUrl": "https://example.com/avatar.jpg",
    "gender": 1,
    "phone": "13800138000",
    "email": "user@example.com",
    "userRole": "user",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  },
  "message": "ok"
}
```

### 3.9 根据ID获取用户脱敏信息

**接口描述**: 获取用户的脱敏信息

- **URL**: `/user/get/vo`
- **请求方法**: GET
- **是否需要登录**: 否

**请求参数**:
- `id` (long, 必填): 用户ID，通过URL参数传递

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "id": 1001,
    "username": "测试用户",
    "avatarUrl": "https://example.com/avatar.jpg",
    "userRole": 1,
    "createTime": "2024-01-01T10:00:00"
  },
  "message": "ok"
}
```

### 3.10 分页获取用户列表（管理员）

**接口描述**: 管理员分页获取用户列表

- **URL**: `/user/list/page`
- **请求方法**: POST
- **是否需要登录**: 是
- **权限要求**: 管理员

**请求参数**:
```json
{
  "current": 1,
  "pageSize": 10,
  "userName": "测试",
  "userRole": "user",
  "sortField": "createTime",
  "sortOrder": "desc"
}
```

**参数说明**:
- `current` (long, 必填): 当前页码，从1开始
- `pageSize` (long, 必填): 每页大小，最大20
- `userName` (string, 可选): 用户昵称，模糊查询
- `userRole` (string, 可选): 用户角色
- `sortField` (string, 可选): 排序字段
- `sortOrder` (string, 可选): 排序方式，asc/desc

**响应示例**: 参考分页响应格式

### 3.11 分页获取用户脱敏信息列表

**接口描述**: 分页获取用户脱敏信息列表

- **URL**: `/user/list/page/vo`
- **请求方法**: POST
- **是否需要登录**: 否

**请求参数**: 同3.10

**响应示例**: 参考分页响应格式，数据为脱敏的用户信息

## 4. 帖子管理接口

### 4.1 创建帖子

**接口描述**: 用户创建新帖子

- **URL**: `/post/add`
- **请求方法**: POST
- **是否需要登录**: 是

**请求参数**:
```json
{
  "title": "帖子标题",
  "content": "帖子内容",
  "tags": ["标签1", "标签2"]
}
```

**参数说明**:
- `title` (string, 必填): 帖子标题
- `content` (string, 必填): 帖子内容
- `tags` (array, 可选): 标签列表

**响应示例**:
```json
{
  "code": 0,
  "data": 2001,
  "message": "ok"
}
```

### 4.2 删除帖子

**接口描述**: 删除帖子（仅作者或管理员可删除）

- **URL**: `/post/delete`
- **请求方法**: POST
- **是否需要登录**: 是

**请求参数**:
```json
{
  "id": 2001
}
```

**响应示例**:
```json
{
  "code": 0,
  "data": true,
  "message": "ok"
}
```

### 4.3 更新帖子（管理员）

**接口描述**: 管理员更新帖子

- **URL**: `/post/update`
- **请求方法**: POST
- **是否需要登录**: 是
- **权限要求**: 管理员

**请求参数**:
```json
{
  "id": 2001,
  "title": "新标题",
  "content": "新内容",
  "tags": ["新标签"]
}
```

**响应示例**:
```json
{
  "code": 0,
  "data": true,
  "message": "ok"
}
```

### 4.4 编辑帖子（用户）

**接口描述**: 用户编辑自己的帖子

- **URL**: `/post/edit`
- **请求方法**: POST
- **是否需要登录**: 是

**请求参数**: 同4.3

**响应示例**: 同4.3

### 4.5 根据ID获取帖子详情

**接口描述**: 获取帖子详细信息

- **URL**: `/post/get/vo`
- **请求方法**: GET
- **是否需要登录**: 否

**请求参数**:
- `id` (long, 必填): 帖子ID，通过URL参数传递

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "id": 2001,
    "title": "帖子标题",
    "content": "帖子内容",
    "thumbNum": 10,
    "favourNum": 5,
    "userId": 1001,
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00",
    "tagList": ["标签1", "标签2"],
    "user": {
      "id": 1001,
      "username": "作者昵称",
      "avatarUrl": "https://example.com/avatar.jpg"
    },
    "hasThumb": false,
    "hasFavour": false
  },
  "message": "ok"
}
```

### 4.6 分页获取帖子列表（管理员）

**接口描述**: 管理员分页获取帖子列表

- **URL**: `/post/list/page`
- **请求方法**: POST
- **是否需要登录**: 是
- **权限要求**: 管理员

**请求参数**:
```json
{
  "current": 1,
  "pageSize": 10,
  "title": "搜索标题",
  "content": "搜索内容",
  "tags": ["标签"],
  "userId": 1001,
  "sortField": "createTime",
  "sortOrder": "desc"
}
```

### 4.7 分页获取帖子列表

**接口描述**: 分页获取帖子列表（公开接口）

- **URL**: `/post/list/page/vo`
- **请求方法**: POST
- **是否需要登录**: 否

**请求参数**: 同4.6

**响应示例**: 参考分页响应格式

### 4.8 分页获取我的帖子列表

**接口描述**: 分页获取当前用户创建的帖子列表

- **URL**: `/post/my/list/page/vo`
- **请求方法**: POST
- **是否需要登录**: 是

**请求参数**: 同4.6（userId会自动设置为当前用户）

### 4.9 分页搜索帖子

**接口描述**: 使用Elasticsearch分页搜索帖子

- **URL**: `/post/search/page/vo`
- **请求方法**: POST
- **是否需要登录**: 否

**请求参数**: 同4.6

## 5. 帖子点赞接口

### 5.1 点赞/取消点赞

**接口描述**: 对帖子进行点赞或取消点赞

- **URL**: `/post_thumb/`
- **请求方法**: POST
- **是否需要登录**: 是

**请求参数**:
```json
{
  "postId": 2001
}
```

**参数说明**:
- `postId` (long, 必填): 帖子ID

**响应示例**:
```json
{
  "code": 0,
  "data": 1,
  "message": "ok"
}
```

**响应数据说明**: 返回点赞变化数，1表示点赞，-1表示取消点赞

## 6. 帖子收藏接口

### 6.1 收藏/取消收藏

**接口描述**: 对帖子进行收藏或取消收藏

- **URL**: `/post_favour/`
- **请求方法**: POST
- **是否需要登录**: 是

**请求参数**:
```json
{
  "postId": 2001
}
```

**响应示例**:
```json
{
  "code": 0,
  "data": 1,
  "message": "ok"
}
```

### 6.2 获取我收藏的帖子列表

**接口描述**: 分页获取当前用户收藏的帖子列表

- **URL**: `/post_favour/my/list/page`
- **请求方法**: POST
- **是否需要登录**: 是

**请求参数**:
```json
{
  "current": 1,
  "pageSize": 10
}
```

### 6.3 获取用户收藏的帖子列表

**接口描述**: 分页获取指定用户收藏的帖子列表

- **URL**: `/post_favour/list/page`
- **请求方法**: POST
- **是否需要登录**: 否

**请求参数**:
```json
{
  "current": 1,
  "pageSize": 10,
  "userId": 1001,
  "postQueryRequest": {
    "title": "搜索标题"
  }
}
```

## 7. 文件上传接口

### 7.1 文件上传

**接口描述**: 上传文件到云存储

- **URL**: `/file/upload`
- **请求方法**: POST
- **是否需要登录**: 是
- **Content-Type**: multipart/form-data

**请求参数**:
- `file` (file, 必填): 要上传的文件
- `biz` (string, 必填): 业务类型，如"user_avatar"

**文件限制**:
- 用户头像：最大1MB，支持jpeg、jpg、svg、png、webp格式

**响应示例**:
```json
{
  "code": 0,
  "data": "https://cos.example.com/user_avatar/1001/abc123-avatar.jpg",
  "message": "ok"
}
```

**响应数据**: 返回文件的访问URL

## 8. 接口调用限制说明

### 8.1 频率限制

- **分页查询**: 每页最大20条记录
- **文件上传**: 用户头像最大1MB
- **搜索接口**: 建议添加防爬虫机制

### 8.2 权限控制

- **管理员接口**: 需要admin角色权限
- **用户接口**: 需要登录，只能操作自己的数据
- **公开接口**: 无需登录，返回脱敏数据

### 8.3 数据安全

- 所有用户密码使用MD5+盐值加密存储
- 公开接口返回脱敏的用户信息
- 敏感操作需要权限验证

## 9. 接口完整性分析

### 9.1 现有接口覆盖的功能

✅ **用户管理**:
- 用户注册、登录、注销
- 用户信息的增删改查
- 用户权限管理
- 分页查询用户列表

✅ **帖子管理**:
- 帖子的增删改查
- 分页查询帖子列表
- 搜索功能（ES）
- 个人帖子管理

✅ **互动功能**:
- 帖子点赞/取消点赞
- 帖子收藏/取消收藏
- 收藏列表查询

✅ **文件管理**:
- 文件上传（头像等）

### 9.2 建议补充的接口

❌ **用户管理增强**:
1. `POST /user/change-password` - 修改密码
2. `POST /user/reset-password` - 重置密码
3. `POST /user/batch/delete` - 批量删除用户（管理员）
4. `GET /user/profile/{id}` - 获取用户详细资料页
5. `POST /user/follow` - 关注用户
6. `POST /user/unfollow` - 取消关注
7. `GET /user/followers/{id}` - 获取粉丝列表
8. `GET /user/following/{id}` - 获取关注列表

❌ **帖子管理增强**:
1. `POST /post/batch/delete` - 批量删除帖子
2. `POST /post/batch/update` - 批量更新帖子状态
3. `GET /post/hot` - 获取热门帖子
4. `GET /post/recommend` - 获取推荐帖子
5. `POST /post/report` - 举报帖子
6. `GET /post/categories` - 获取帖子分类
7. `GET /post/tags` - 获取热门标签

❌ **评论系统**:
1. `POST /comment/add` - 添加评论
2. `POST /comment/delete` - 删除评论
3. `POST /comment/update` - 更新评论
4. `GET /comment/list/{postId}` - 获取帖子评论列表
5. `POST /comment/reply` - 回复评论
6. `POST /comment/thumb` - 点赞评论

❌ **通知系统**:
1. `GET /notification/list` - 获取通知列表
2. `POST /notification/read` - 标记通知已读
3. `POST /notification/read/all` - 全部标记已读
4. `GET /notification/unread/count` - 获取未读通知数量

❌ **统计分析**:
1. `GET /stats/user` - 用户统计信息
2. `GET /stats/post` - 帖子统计信息
3. `GET /stats/daily` - 每日活跃统计
4. `GET /stats/popular` - 热门内容统计

❌ **系统管理**:
1. `GET /admin/dashboard` - 管理员仪表板
2. `POST /admin/user/ban` - 封禁用户
3. `POST /admin/user/unban` - 解封用户
4. `GET /admin/logs` - 系统日志查询
5. `POST /admin/announcement` - 发布公告

❌ **搜索增强**:
1. `GET /search/suggest` - 搜索建议
2. `GET /search/history` - 搜索历史
3. `POST /search/save` - 保存搜索

### 9.3 优先级建议

**高优先级**（前端开发必需）:
1. 修改密码接口
2. 评论系统完整接口
3. 通知系统基础接口
4. 热门帖子和推荐接口

**中优先级**（用户体验提升）:
1. 用户关注系统
2. 帖子举报功能
3. 搜索建议和历史
4. 批量操作接口

**低优先级**（管理和分析）:
1. 统计分析接口
2. 系统管理接口
3. 高级搜索功能

## 10. 开发建议

1. **接口版本控制**: 建议在URL中加入版本号，如`/api/v1/user/login`
2. **接口文档自动化**: 建议集成Swagger/OpenAPI自动生成接口文档
3. **错误处理统一**: 建议完善全局异常处理机制
4. **接口测试**: 建议补充完整的单元测试和集成测试
5. **性能优化**: 对高频接口添加缓存机制
6. **安全加固**: 添加接口访问频率限制和防刷机制