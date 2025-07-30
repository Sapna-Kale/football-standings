# Use a supported base image
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy built JAR into image
COPY target/football-standings-*.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
