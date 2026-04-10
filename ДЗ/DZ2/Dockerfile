FROM gradle:8.7-jdk17 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

RUN gradle clean installDist --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/build/install/dz2-multi-db /app/dz2-multi-db

ENTRYPOINT ["/app/dz2-multi-db/bin/dz2-multi-db"]
