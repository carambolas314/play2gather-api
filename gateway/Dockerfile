FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/gateway-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
