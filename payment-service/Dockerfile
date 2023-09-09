FROM eclipse-temurin:17

LABEL manteiner="pablo.sanchez_bello@mercedes-benz.com"

WORKDIR /app

COPY target/payment-service-0.0.1-SNAPSHOT.jar /app/payment-service.jar

ENTRYPOINT ["java", "-jar", "payment-service.jar"]