# Multi-stage Dockerfile for Spring Boot (Java 17)

# ===== Stage 1: Build the application =====
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Leverage Docker cache: first copy only the pom.xml and resolve dependencies
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -e -DskipTests dependency:go-offline

# Now copy the source and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -e -DskipTests package

# ===== Stage 2: Run the application =====
FROM eclipse-temurin:17.0.16_8-jre

# Create a non-root user
#RUN addgroup -S app && adduser -S app -G app
WORKDIR /app

# Copy the fat jar from builder image
# Spring Boot Maven plugin creates a single executable jar in target directory
COPY --from=builder /workspace/target/*SNAPSHOT.jar /app/app.jar

# Default runtime configuration (can be overridden at runtime)
ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE="" \
    TZ=UTC

# Expose the default Spring Boot port; can be changed via server.port
EXPOSE 8080

#USER app

# Health-friendly start command
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
