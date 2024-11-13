pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // Add JitPack here
        maven("https://plugins.gradle.org/m2/")
        maven("https://clojars.org/repo/")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // Add JitPack here as well
        maven("https://maven.google.com")
        maven("https://clojars.org/repo/")
    }
}

rootProject.name = "Kawaime"
include(":app")
