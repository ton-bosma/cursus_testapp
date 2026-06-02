# ── Stage 1: Build ────────────────────────────────────────────────────────
# Build context is the repo root so the Gradle wrapper and settings.gradle
# are available; the backend module is :backend.
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace

# Copy Gradle wrapper and project metadata first (better layer caching)
COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY settings.gradle ./

# Copy backend module sources
COPY backend/ backend/

# Make gradlew executable and build the backend bootJar
RUN chmod +x gradlew && \
    ./gradlew :backend:bootJar --no-daemon -q

# ── Stage 2: Runtime ──────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S viool && adduser -S viool -G viool

# Copy the executable jar from the build stage
COPY --from=builder /workspace/backend/build/libs/*.jar app.jar

# Create the file-storage directory so the mount point exists even without a volume
RUN mkdir -p /app/storage && chown -R viool:viool /app

USER viool

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
