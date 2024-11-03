# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the jar file from the target directory to the container
COPY target/Notification-0.0.1.jar notification-service.jar

# Expose the port the app runs on
EXPOSE 8085

# Run the jar file
ENTRYPOINT ["java", "-jar", "notification-service.jar"]
