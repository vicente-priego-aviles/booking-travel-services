FROM eclipse-temurin:17

LABEL manteiner="pablo.sanchez_bello@mercedes-benz.com"

WORKDIR /app

COPY target/car-service-0.0.1-SNAPSHOT.jar /app/car-service.jar

ENTRYPOINT ["java", "-Dspring.cloud.stream.default-binder=${MESSAGE_BROKER}", "-jar", "car-service.jar"]