# Stage 1: Build với Gradle
FROM gradle:8-jdk17-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar -x test
RUN chmod +x gradlew

# Stage 2: Chạy ứng dụng
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
