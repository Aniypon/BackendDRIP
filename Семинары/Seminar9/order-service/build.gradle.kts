plugins {
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.3"
    java
    id("org.openapi.generator") version "7.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.7.7")
    
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation("net.ttddyy.observation:datasource-micrometer-spring-boot:1.0.5")

    
    implementation("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("io.swagger.core.v3:swagger-annotations-jakarta:2.2.19")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("io.github.openfeign:feign-jackson:13.5")
    implementation("io.github.openfeign:feign-okhttp:13.5")
    implementation("io.github.openfeign:feign-micrometer:13.5")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    
    // SpringDoc OpenAPI for automatic OpenAPI spec generation from controllers
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.4")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$rootDir/user-service.yaml")
    outputDir.set("$buildDir/generated")
    
    configOptions.set(mapOf(
        "library" to "spring-cloud",
        "useSpringBoot3" to "true",
        "useTags" to "true",
        "apiPackage" to "org.example.api.client",
        "modelPackage" to "org.example.model.client"
    ))
    
    sourceSets.main {
        java.srcDir("$buildDir/generated/src/main/java")
    }
}

tasks.compileJava {
    dependsOn(tasks.openApiGenerate)
}
