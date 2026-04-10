FROM gradle:8.5-jdk21 AS cache
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./

RUN gradle clean build --no-daemon -x test || true


FROM gradle:8.5-jdk21 AS build
WORKDIR /app

COPY --from=cache /home/gradle/.gradle /home/gradle/.gradle
COPY user-service.yaml build.gradle.kts settings.gradle.kts ./
COPY src ./src

RUN gradle build


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
