# 使用 JDK 基础镜像
FROM openjdk:17-jdk-slim

# 拷贝本地的 reggie.jar 到容器中
COPY reggie-1.0-SNAPSHOT.jar reggie.jar

# 设置环境变量（OSS 的 AK/SK 可以通过 env 注入）
ENV ALIBABA_CLOUD_ACCESS_KEY_ID=L
ENV ALIBABA_CLOUD_ACCESS_KEY_SECRET=F

# 设置时区（可选，确保日志时间一致）
ENV TZ=Asia/Shanghai
# 安装 tzdata 并配置时区
RUN apt-get update && \
    apt-get install -y tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 设置语言环境和编码格式
ENV LANG=C.UTF-8 \
    LANGUAGE=C.UTF-8 \
    LC_ALL=C.UTF-8

# 运行应用并指定配置文件
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8","-jar", "reggie.jar", "--spring.profiles.active=test"]
