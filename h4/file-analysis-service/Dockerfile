FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

COPY gradlew gradlew.bat ./
COPY gradle/wrapper gradle/wrapper
RUN chmod +x gradlew

RUN --mount=type=cache,target=/root/.gradle-analysis \
    ./gradlew --version

COPY build.gradle.kts settings.gradle.kts ./
COPY file-analysis-service/build.gradle.kts file-analysis-service/

RUN --mount=type=cache,target=/root/.gradle-analysis \
    ./gradlew :file-analysis-service:dependencies --no-daemon

COPY file-analysis-service/src file-analysis-service/src/

RUN --mount=type=cache,target=/root/.gradle-analysis \
    ./gradlew :file-analysis-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/file-analysis-service/build/libs/file-analysis-service-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]