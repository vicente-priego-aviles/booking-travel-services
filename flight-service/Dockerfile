FROM eclipse-temurin:17

LABEL manteiner="pablo.sanchez_bello@mercedes-benz.com"

WORKDIR /app

COPY target/flight-service-0.0.1-SNAPSHOT.jar /app/flight-service.jar

ENTRYPOINT ["java", "-jar", "flight-service.jar"]