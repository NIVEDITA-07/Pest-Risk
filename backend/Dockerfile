# Multi-stage Docker build for PestRisk Spring Boot Backend on Render / Cloud
# Stage 1: Build JAR using Eclipse Temurin Java 21 & Maven
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy Maven wrapper & pom.xml first to cache dependencies
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn/
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy backend source code and build production jar
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime image (lightweight JRE 21)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create directory for persistent SQLite database
RUN mkdir -p /app/data

# Copy compiled jar from build stage
COPY --from=build /app/target/pestrisk-backend-0.0.1-SNAPSHOT.jar app.jar

# Render exposes dynamic PORT (default: 8080 or 10000)
ENV PORT=8080
ENV DB_URL=jdbc:sqlite:/app/data/pestrisk.db
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
