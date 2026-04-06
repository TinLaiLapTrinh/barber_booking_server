# Bước 1: Sử dụng hình ảnh JDK để chạy app
FROM eclipse-temurin:17-jdk-alpine

# Bước 2: Tạo thư mục làm việc trong container
WORKDIR /app

# Bước 3: Copy file JAR đã build (trong thư mục target) vào container
# Tín nhớ chạy lệnh ./mvnw package trước nhé
COPY target/*.jar app.jar

# Bước 4: Mở cổng 8080
EXPOSE 8080

# Bước 5: Lệnh để chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]