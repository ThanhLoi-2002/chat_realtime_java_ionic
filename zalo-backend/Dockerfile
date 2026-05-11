# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM openjdk:17-jdk-slim
RUN mkdir -p /app && chmod 777 /app
WORKDIR /app

# Thay đổi dòng này để đảm bảo lấy đúng file Spring Boot Jar
COPY --from=build /target/*.jar app.jar

# Kiểm tra xem file jar có nội dung không (optional)
RUN jar -tf app.jar | grep Main-Class || echo "Warning: No Main Class found"

ENV PORT=7860
EXPOSE 7860
ENTRYPOINT ["java", "-jar", "app.jar"]