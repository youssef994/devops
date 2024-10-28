# Use an OpenJDK base image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file into the container
COPY target/Question-Anwser-0.0.1-SNAPSHOT.jar question-anwser.jar

# Expose the service port (8088)
EXPOSE 8088

# Run the Question-Anwser service
ENTRYPOINT ["java", "-jar", "question-anwser.jar"]
