FROM eclipse-temurin:17

LABEL manteiner="pablo.sanchez_bello@company.com"

WORKDIR /app

COPY target/api-gateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar

EXPOSE 9191

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]