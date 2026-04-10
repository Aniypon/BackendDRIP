rootProject.name = "user-service"

pluginManagement {
    repositories {
        maven(url = "https://artifactory.tcsbank.ru/artifactory/maven-all")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://artifactory.tcsbank.ru/artifactory/maven-all")
    }
}

buildscript {
    repositories {
        maven(url = "https://artifactory.tcsbank.ru/artifactory/maven-all")
    }
}
