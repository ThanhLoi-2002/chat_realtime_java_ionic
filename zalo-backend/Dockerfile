# Stage 1: Build với Maven
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Chạy app với JDK nhẹ
FROM openjdk:17-jdk-slim
COPY --from=build /target/*.jar app.jar

# Giới hạn bộ nhớ để không bị crash trên gói Free (256MB-512MB RAM)
ENV JAVA_TOOL_OPTIONS="-Xmx200M -Xms150M"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]