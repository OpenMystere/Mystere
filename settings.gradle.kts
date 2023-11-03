pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }
    versionCatalogs {
        val mystere by creating {
            from(files(File(rootDir, "./gradle/mystere.versions.toml")))
        }
    }
}

rootProject.name = "Mystere"

include(":mystere")
