# Stage 1: Build với Gradle
FROM gradle:8-jdk17-alpine AS build
WORKDIR /app

# Copy toàn bộ code vào
COPY . .

# PHẢI CẤP QUYỀN TRƯỚC KHI CHẠY
RUN chmod +x gradlew

# Sau đó mới build Jar
RUN ./gradlew bootJar -x test

# Stage 2: Chạy ứng dụng
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy file jar từ stage build sang
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
