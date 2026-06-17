FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

COPY gradlew gradlew.bat ./
COPY gradle/wrapper gradle/wrapper
RUN chmod +x gradlew

RUN --mount=type=cache,target=/root/.gradle-gateway \
    ./gradlew --version

COPY build.gradle.kts settings.gradle.kts ./
COPY api-gateway/build.gradle.kts api-gateway/

RUN --mount=type=cache,target=/root/.gradle-gateway \
    ./gradlew :api-gateway:dependencies --no-daemon

COPY api-gateway/src api-gateway/src/

RUN --mount=type=cache,target=/root/.gradle-gateway \
    ./gradlew :api-gateway:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/api-gateway/build/libs/api-gateway-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]