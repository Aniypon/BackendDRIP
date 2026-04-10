plugins {
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    java
}

group = "org.example"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Реляционное хранилище (PostgreSQL через JPA/Hibernate)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql:42.7.7")

    // Документное хранилище (MongoDB)
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    // Key-value / кэш (Redis)
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Колоночное OLAP-хранилище (ClickHouse)
    implementation("com.clickhouse:clickhouse-jdbc:0.6.5")
    implementation("com.clickhouse:clickhouse-http-client:0.6.5")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")

    // Объектное хранилище (S3 / MinIO)
    implementation("software.amazon.awssdk:s3:2.29.0")
    implementation("software.amazon.awssdk:url-connection-client:2.29.0")

    // Дополнительное задание: in-memory data grid (Hazelcast, embedded)
    implementation("com.hazelcast:hazelcast:5.3.6")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.bootJar {
    mainClass.set("org.example.storage.Main")
}
