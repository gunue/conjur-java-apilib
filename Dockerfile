# Use the official Maven image to build the Java code
FROM maven:3.8.2-openjdk-11 AS build

# Copy the Java code and the pom.xml file into the image
COPY src/main/java/SecretFetcher.java src/main/java/
COPY pom.xml .

# Package the Java code and its dependencies into a fat JAR
RUN mvn package

# Use the official OpenJDK image to run the Java code
FROM openjdk:11-jre-slim

# Copy the fat JAR from the build image
COPY --from=build /target/SecretFetcher-1.0-SNAPSHOT-jar-with-dependencies.jar /app/

# Set the working directory
WORKDIR /app

# Run the Java application
CMD ["java", "-jar", "SecretFetcher-1.0-SNAPSHOT-jar-with-dependencies.jar"]

