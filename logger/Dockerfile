FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/logger-1.0.0.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]