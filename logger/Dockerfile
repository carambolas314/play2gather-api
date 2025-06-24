FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/logger-1.0.0.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
