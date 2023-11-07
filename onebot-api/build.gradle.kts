plugins {
    alias(mystere.plugins.kotlin.multiplatform)
    alias(mystere.plugins.kotlin.plugin.serialization)
}

kotlin {
    jvm {
        jvmToolchain(21)
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
    macosArm64()
    macosX64()
//    linuxArm64()
    linuxX64()
    mingwX64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        // common
        val commonMain by getting {
            dependencies {
                implementation(mystere.kotlin.reflect)
                implementation(mystere.kotlin.stdlib)
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
