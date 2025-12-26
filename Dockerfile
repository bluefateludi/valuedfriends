# Docker 镜像构建 - 微信云托管优化版
# @author <a href="https://github.com/liyupi">程序员鱼皮</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>

# 构建阶段 - 使用官方镜像（通过镜像加速器拉取）
FROM maven:3.8.1-openjdk-8 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 并下载依赖（利用Docker缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用（跳过测试编译和执行）
RUN mvn clean package -Dmaven.test.skip=true

# 运行阶段 - 使用官方镜像（通过镜像加速器拉取）
FROM eclipse-temurin:8-jre-alpine

# 创建非root用户（安全最佳实践）
RUN addgroup -g 1000 appuser && adduser -D -u 1000 -G appuser appuser

# 设置工作目录
WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=builder /app/target/valuedfriends-0.0.1-SNAPSHOT.jar app.jar

# 更改文件所有者
RUN chown appuser:appuser app.jar

# 切换到非root用户
USER appuser

# 暴露端口（应用使用 8101 端口）
EXPOSE 8101

# 设置JVM参数优化内存使用
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport"

# 启动应用
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=8101 --spring.profiles.active=prod"]