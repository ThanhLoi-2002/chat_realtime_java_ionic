# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM openjdk:17-jdk-slim
# Tạo thư mục và cấp quyền để tránh lỗi Permission
RUN mkdir -p /app && chmod 777 /app
WORKDIR /app
COPY --from=build /target/*.jar app.jar

# Hugging Face Spaces mặc định dùng port 7860
ENV PORT=7860
EXPOSE 7860

ENTRYPOINT ["java", "-jar", "app.jar"]