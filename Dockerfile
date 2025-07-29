# 1. 베이스 이미지
FROM openjdk:17-slim

# 2. Redis 설치를 위한 패키지 업데이트 및 설치
RUN apt-get update && \
    apt-get install -y redis-server && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 3. JAR 파일 복사
ARG JAR_PATH=build/libs/*.jar
COPY ${JAR_PATH} /app/server.jar

# 4. 포트 오픈 (Spring Boot: 9000, Redis: 6379)
EXPOSE 9000 6379

# 5. Redis 실행 + Spring Boot 실행
ENTRYPOINT sh -c "redis-server --daemonize yes && java -jar /app/server.jar"