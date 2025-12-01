# ---------- 1단계: 빌드 ----------
FROM gradle:8.8-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

# ---------- 2단계: 실행 환경 ----------
# openjdk:17-jdk-slim → eclipse-temurin:17-jdk (공식 후속)
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
COPY food_logs.json food_logs.json

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
