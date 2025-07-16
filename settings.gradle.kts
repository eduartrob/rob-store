pluginManagement {
    repositories {
        // No pongas filtros aquí para no bloquear KSP ni otros plugins
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "robStore"
include(":app")
