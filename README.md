# ValuedFriends - 基于标签的智能推荐系统
//常用网址
`http://localhost:8101/api/doc.html
> 作者：[程序员鱼皮](https://github.com/liyupi)
> 仅分享于 [编程导航知识星球](https://yupi.icu)

基于 Java SpringBoot 的智能推荐系统，整合了常用框架和主流业务的示例代码。

专注于基于用户标签的好友推荐和商品推荐功能，帮助用户发现志同道合的朋友和感兴趣的商品。

[toc]

## 模板特点

### 主流框架 & 特性

- Spring Boot 2.7.x（贼新）
- Spring MVC
- MyBatis + MyBatis Plus 数据访问（开启分页）
- Spring Boot 调试工具和项目处理器
- Spring AOP 切面编程
- Spring Scheduler 定时任务
- Spring 事务注解

### 数据存储

- MySQL 数据库
- Redis 内存数据库
- Elasticsearch 搜索引擎
- 腾讯云 COS 对象存储

### 工具类

- Easy Excel 表格处理
- Hutool 工具库
- Apache Commons Lang3 工具类
- Lombok 注解

### 业务特性

- 业务代码生成器（支持自动生成 Service、Controller、数据模型代码）
- Spring Session Redis 分布式登录
- 全局请求响应拦截器（记录日志）
- 全局异常处理器
- 自定义错误码
- 封装通用响应类
- Swagger + Knife4j 接口文档
- 自定义权限注解 + 全局校验
- 全局跨域处理
- 长整数丢失精度解决
- 多环境配置


## 业务功能

- 提供示例 SQL（用户、用户标签、商品、推荐记录表）
- 用户登录、注册、注销、更新、检索、权限管理
- 用户标签管理：标签创建、编辑、删除、分类管理
- 基于用户标签的好友推荐系统：
  - 智能匹配算法，根据用户兴趣标签推荐相似用户
  - 推荐结果排序和过滤
  - 好友申请和管理功能
- 基于用户标签的商品推荐功能：
  - 个性化商品推荐算法
  - 商品标签体系管理
  - 推荐历史记录和反馈机制
- 推荐算法优化：协同过滤、内容过滤、混合推荐
- 支持微信开放平台登录
- 支持微信公众号订阅、收发消息、设置菜单
- 支持分业务的文件上传

### 单元测试

- JUnit5 单元测试
- 示例单元测试类

### 架构设计

- 合理分层


## 快速上手

> 所有需要修改的地方鱼皮都标记了 `todo`，便于大家找到修改的位置~

### MySQL 数据库

1）修改 `application.yml` 的数据库配置为你自己的：

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/my_db
    username: root
    password: 123456
```

2）执行 `sql/create_table.sql` 中的数据库语句，自动创建库表（包含用户表、用户标签表、商品表、推荐记录表等）

3）启动项目，访问 `http://localhost:8101/api/doc.html` 即可打开接口文档，不需要写前端就能在线调试接口了~

![](doc/swagger.png)

### Redis 分布式登录

1）修改 `application.yml` 的 Redis 配置为你自己的：

```yml
spring:
  redis:
    database: 1
    host: localhost
    port: 6379
    timeout: 5000
    password: 123456
```

2）修改 `application.yml` 中的 session 存储方式：

```yml
spring:
  session:
    store-type: redis
```

3）移除 `MainApplication` 类开头 `@SpringBootApplication` 注解内的 exclude 参数：

修改前：

```java
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
```

修改后：


```java
@SpringBootApplication
```

### 推荐系统配置

1）配置推荐算法参数，修改 `application.yml`：

```yml
recommendation:
  friend:
    similarity-threshold: 0.6
    max-recommendations: 20
  product:
    algorithm: hybrid
    weight-collaborative: 0.6
    weight-content: 0.4
```

2）初始化推荐系统数据

执行 `sql/insert_test_data.sql` 中的测试数据，包含用户标签和商品标签的示例数据。

3）开启推荐任务

找到 job 目录下的推荐任务文件，取消掉 `@Component` 注解的注释：

```java
// todo 取消注释开启推荐任务
//@Component
```

### 业务代码生成器

支持自动生成 Service、Controller、数据模型代码，配合 MyBatisX 插件，可以快速开发增删改查等实用基础功能。

找到 `generate.CodeGenerator` 类，修改生成参数和生成路径，并且支持注释掉不需要的生成逻辑，然后运行即可。

```
// 指定生成参数
String packageName = "com.yupi.valuedfriends";
String dataName = "用户标签";
String dataKey = "userTag";
String upperDataKey = "UserTag";
```

生成代码后，可以移动到实际项目中，并且按照 `// todo` 注释的提示来针对自己的业务需求进行修改。
