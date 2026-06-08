FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN chmod +x gradlew

RUN --mount=type=cache,target=/root/.gradle-scheduler \
    GRADLE_USER_HOME=/root/.gradle-scheduler ./gradlew dependencies --no-daemon

COPY src ./src

RUN --mount=type=cache,target=/root/.gradle-scheduler \
    GRADLE_USER_HOME=/root/.gradle-scheduler ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
