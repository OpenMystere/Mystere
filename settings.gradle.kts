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

        maven("https://raw.githubusercontent.com/krzema12/snakeyaml-engine-kmp/artifacts/m2/") {
            name = "SnakeYAML Engine KMP Snapshots"
            mavenContent {
                // only include the relevant snapshots
                includeGroup("it.krzeminski")
                snapshotsOnly()
            }
        }
    }
    versionCatalogs {
        val mystere by creating {
            from(files(File(rootDir, "./gradle/mystere.versions.toml")))
        }
    }
}

rootProject.name = "Mystere"

include(":onebot-api")
include(":onebot-v11")
include(":onebot-v12")

include(":mystere")
include(":mystere-core")
include(":mystere-qq")

include(":sdk-qq")

include(":kotlinx-serialization-cqcode")
