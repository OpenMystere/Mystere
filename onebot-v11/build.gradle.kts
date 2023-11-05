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

    sourceSets {
        // common
        val commonMain by getting {
            dependencies {
                implementation(mystere.kotlin.reflect)
                implementation(mystere.kotlin.stdlib)

                implementation(mystere.ktor.plugin.logging)

                implementation(mystere.kotlinx.coroutines.core)

                implementation(project(":onebot-api"))
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
        val macosMain by creating {
            dependsOn(commonMain)
            macosArm64Main.dependsOn(this)
            macosX64Main.dependsOn(this)
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
        val linuxMain by creating {
            dependsOn(commonMain)
//            linuxArm64Main.dependsOn(this)
            linuxX64Main.dependsOn(this)
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
