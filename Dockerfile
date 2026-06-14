# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
EXPOSE 8080

WORKDIR /usr/app
COPY ./target/java-maven-app-*.jar /usr/app

CMD java -jar java-maven-app-*.jar