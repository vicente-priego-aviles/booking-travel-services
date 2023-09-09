FROM eclipse-temurin:17

LABEL manteiner="pablo.sanchez_bello@mercedes-benz.com"

WORKDIR /app

COPY target/api-gateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]