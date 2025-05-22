# Build stage
FROM gradle:8.5-jdk21-alpine AS build
WORKDIR /app

# Gradle 캐시를 활용하기 위해 의존성 파일만 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# 각 모듈의 build.gradle 복사
COPY common/build.gradle ./common/
COPY account/build.gradle ./account/
COPY transaction/build.gradle ./transaction/

# 의존성 다운로드 (캐시 활용)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY . .

# 빌드 수행
RUN ./gradlew clean build -x test --no-daemon

# Runtime stage
FROM openjdk:21-jdk-slim
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/wirebarley-*.jar app.jar

# 애플리케이션 실행 사용자 생성
RUN useradd -r -u 1001 appuser
USER appuser

# 포트 설정
EXPOSE 8080

# JVM 옵션 설정
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
