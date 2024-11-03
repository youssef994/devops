# Use an OpenJDK base image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file into the container
COPY target/api-gateway-0.0.1.jar api-gateway.jar

# Expose the gateway port (8081)
EXPOSE 8081

# Run the API Gateway
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]
