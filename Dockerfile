FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/Money_Flow.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]