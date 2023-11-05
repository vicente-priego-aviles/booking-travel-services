FROM eclipse-temurin:17

LABEL manteiner="pablo.sanchez_bello@company.com"

WORKDIR /app

COPY target/config-server-0.0.1-SNAPSHOT.jar /app/config-server.jar

ENTRYPOINT ["java", "-jar", "config-server.jar"]