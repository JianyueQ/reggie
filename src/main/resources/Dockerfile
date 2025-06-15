# 使用 JDK 基础镜像
FROM openjdk:17-jdk-slim
WORKDIR /app

# 拷贝应用 jar 包
COPY reggie.jar app.jar

# 设置环境变量（OSS 的 AK/SK 可以通过 env 注入）
ENV ALIBABA_CLOUD_ACCESS_KEY_ID=your_oss_key_id
ENV ALIBABA_CLOUD_ACCESS_KEY_SECRET=your_oss_key_secret

# 运行应用并指定配置文件
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=test"]
