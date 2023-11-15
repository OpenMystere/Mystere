plugins {
    alias(mystere.plugins.kotlin.multiplatform)
    alias(mystere.plugins.kotlin.plugin.serialization)
    alias(mystere.plugins.buildkonfig)
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
    macosArm64()
    macosX64()
    linuxArm64()
    linuxX64()
    mingwX64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        // common
        val commonMain by getting {
            dependencies {
                implementation(mystere.kotlin.reflect)
                implementation(mystere.kotlin.stdlib)
                implementation(mystere.kotlinx.serialization.core)
                implementation(mystere.kotlinx.serialization.json)
                implementation(project(":mystere-util"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(mystere.kotlin.test)
            }
        }

        // jvm
        val jvmMain by getting {
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
        val linuxArm64Main by getting {
            dependencies {

            }
        }
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

buildkonfig {
    packageName = findProperty("mystere.lib.serialization.cqcode.pkgName")!!.toString()

    defaultConfigs {

    }
}
