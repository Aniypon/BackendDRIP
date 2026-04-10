plugins {
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.3"
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }
}

repositories {
    maven("https://artifactory.tcsbank.ru/artifactory/maven-all")
    maven("https://artifactory.tcsbank.ru/artifactory/maven-releases-hosted/ins-integration")
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.7")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    implementation("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("com.clickhouse:clickhouse-jdbc:0.7.1")
    implementation("org.lz4:lz4-java:1.8.0")

    implementation("org.springframework.data:spring-data-redis")
    implementation("redis.clients:jedis:5.1.2")

    implementation("software.amazon.awssdk:s3:2.29.0")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("io.jsonwebtoken:jjwt:0.13.0")
}

tasks.test {
    useJUnitPlatform()
}
