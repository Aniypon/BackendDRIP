plugins {
    application
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

application {
    mainClass.set("org.example.Main")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
