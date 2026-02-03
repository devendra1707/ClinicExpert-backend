# -------- Build Stage --------
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# -------- Runtime Stage --------
FROM eclipse-temurin:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/ClinicExperts-0.0.1-SNAPSHOT.jar ClinicExperts.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","ClinicExperts.jar"]
