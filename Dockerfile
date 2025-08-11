# 1. 베이스 이미지
FROM openjdk:17-slim

# 2. JAR 파일 복사
ARG JAR_PATH=build/libs/*.jar
COPY ${JAR_PATH} /app/server.jar

# 3. 포트 오픈 (Spring Boot: 9000, Redis: 6379)
EXPOSE 9000

# 4. Redis 실행 + Spring Boot 실행
ENTRYPOINT sh -c "java -jar /app/server.jar"