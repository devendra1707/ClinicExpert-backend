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
