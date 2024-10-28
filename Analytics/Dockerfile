
FROM openjdk:17-jdk-alpine


WORKDIR /app


COPY target/Analytics-0.0.1-SNAPSHOT.jar app.jar


EXPOSE 8087


ENTRYPOINT ["java", "-jar", "app.jar"]
