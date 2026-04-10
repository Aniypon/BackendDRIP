rootProject.name = "Seminar9"

pluginManagement {
    repositories {
        maven(url = "https://artifactory.tcsbank.ru/artifactory/maven-all")
        // gradlePluginPortal()
        // mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://artifactory.tcsbank.ru/artifactory/maven-all")
        // mavenCentral()
    }
}

buildscript {
    repositories {
        maven(url = "https://artifactory.tcsbank.ru/artifactory/maven-all")
        // mavenCentral()
    }
}
