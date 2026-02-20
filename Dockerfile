# Stage 1: Build
# ================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first (better layer caching)
# If dependencies haven't changed, Docker reuses this cached layer
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code and build the JAR
COPY src src
RUN ./mvnw package -DskipTests -B

# ================================
# Stage 2: Run
# ================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the final JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Document the port the app listens on
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]