FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/iam-1.0.0.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
