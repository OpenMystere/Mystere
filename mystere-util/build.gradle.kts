plugins {
    alias(mystere.plugins.kotlin.multiplatform)
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
                api(mystere.kotlin.logging)
            }
        }

        // jvm
        val jvmMain by getting {
            dependencies {
                implementation(mystere.slf4j.api)
                implementation(mystere.logback.classic)
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
    packageName = findProperty("mystere.lib.util.pkgName")!!.toString()

    defaultConfigs {

    }
}
