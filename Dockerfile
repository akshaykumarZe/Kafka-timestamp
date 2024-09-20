# Use an official Maven image to build the project
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the project
RUN mvn clean package -DskipTests

# Use an official OpenJDK image for the runtime
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/kafka-timestamp-processor-0.0.1-SNAPSHOT.jar .

# Command to run the application
CMD ["java", "-jar", "kafka-timestamp-processor-0.0.1-SNAPSHOT.jar"]
