# Use an OpenJDK base image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file into the container
COPY target/Eureka-server-0.0.1-SNAPSHOT.jar eureka-server.jar

# Expose the Eureka port (8761)
EXPOSE 8761

# Run the Eureka server
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]
