# -------- Build Stage --------
FROM maven:3.9.3-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy all files to container
COPY . .

# Build the Spring Boot jar (skip tests for faster build)
RUN mvn clean package -DskipTests

# -------- Run Stage --------
FROM eclipse-temurin:21-jdk-alpine

# Set workdir
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/ClinicExperts-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render will use PORT env variable)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
