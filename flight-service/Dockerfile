FROM eclipse-temurin:17

LABEL manteiner="pablo.sanchez_bello@mercedes-benz.com"

WORKDIR /app

COPY target/flight-service-0.0.1-SNAPSHOT.jar /app/flight-service.jar

ENTRYPOINT ["java", "-Dspring.cloud.stream.default-binder=${MESSAGE_BROKER}", "-jar", "flight-service.jar"]