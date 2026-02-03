<<<<<<< HEAD
# -------- Build Stage --------
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# -------- Runtime Stage --------
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
=======
# -------- Build Stage (Maven + JDK 21) --------
FROM maven:3.9.3-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# -------- Runtime Stage (JDK 21) --------
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/ClinicExperts-0.0.1-SNAPSHOT.jar ClinicExperts.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","ClinicExperts.jar"]
>>>>>>> c5077f3127947e5c11c916d262d52caa99fe936d
