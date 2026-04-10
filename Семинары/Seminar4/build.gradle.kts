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
}

tasks.test {
    useJUnitPlatform()
}
