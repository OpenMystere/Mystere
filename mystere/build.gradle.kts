plugins {
    alias(mystere.plugins.kotlin.multiplatform)
    alias(mystere.plugins.kotlin.plugin.serialization)
}

kotlin {
    jvm {
        jvmToolchain(17)
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
    listOf(
        macosArm64(),
        macosX64(),
        // TODO: clikt
//        linuxArm64(),
        linuxX64(),
        mingwX64(),
    ).forEach {
        it.binaries.executable {
            baseName = "mystere"
            entryPoint = "io.github.mystere.app.main"
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        // common
        val commonMain by getting {
            dependencies {
                implementation(mystere.kotlin.reflect)
                implementation(mystere.kotlin.stdlib)
                implementation(mystere.kotlin.logging)

                implementation(mystere.ktor.plugin.serialization.kotlinx.json)

                implementation(mystere.kotlinx.coroutines.core)
                implementation(mystere.kotlinx.io.core)
                implementation(mystere.clikt)
                implementation(mystere.yamlkt)

                implementation(project(":mystere-core"))
                implementation(project(":mystere-qq"))
            }
        }

        // jvm
        val jvmMain by getting {
            dependencies {
                implementation(mystere.logback.classic)
            }
        }

        // native
        val nativeMain by getting {
            dependencies {

            }
        }

        // macos
        val macosArm64Main by getting {
            dependencies {

            }
        }
        val macosX64Main by getting {
            dependencies {

            }
        }
        val macosMain by getting {
            dependencies {

            }
        }

        // linux
        val linuxX64Main by getting {
            dependencies {

            }
        }
//        val linuxArm64Main by getting {
//            dependencies {
//
//            }
//        }
        val linuxMain by getting {
            dependencies {

            }
        }

        // windows
        val mingwX64Main by getting {
            dependencies {

            }
        }
    }
}

// https://github.com/JetBrains/compose-multiplatform/issues/3123#issuecomment-1699296352
tasks.configureEach {
    if (name == "jvmRun" || name.contains("run(.*?)Executable".toRegex())) {
        (this as ProcessForkOptions).workingDir = project.file("bin")
    }
}
