FROM eclipse-temurin:17

LABEL manteiner="pablo.sanchez_bello@company.com"

WORKDIR /app

COPY target/hotel-service-0.0.1-SNAPSHOT.jar /app/hotel-service.jar

ENTRYPOINT ["java", "-Dspring.cloud.stream.default-binder=${MESSAGE_BROKER}", "-jar", "hotel-service.jar"]