# ---- 1) Build stage: Gradle로 JAR 생성 ----
FROM gradle:8.7-jdk17-alpine AS build
WORKDIR /workspace

# 컨텍스트가 이미 AirBnG/ 이므로 그대로 복사
COPY . ./

# 멀티모듈 기준: 메인 앱 모듈이 airbng-app
# 테스트는 빌드 속도 때문에 제외(-x test)
RUN gradle --no-daemon clean :airbng-app:bootJar -x test

# ---- 2) Runtime stage: 가볍게 JRE만 사용 ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 빌드 산출물(JAR) 복사
COPY --from=build /workspace/airbng-app/build/libs/*.jar /app/server.jar

# 컨테이너 내부에서 백엔드는 9000 포트로 리슨
EXPOSE 9000

# 필요시 JAVA_OPTS로 힙/GC 등 전달 가능
ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/server.jar"]