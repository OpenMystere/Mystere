import com.codingfeline.buildkonfig.compiler.FieldSpec

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
                implementation(mystere.ktor.client.core)
                implementation(mystere.ktor.plugin.logging)
                implementation(mystere.ktor.client.content.negotiation)
                implementation(mystere.ktor.plugin.serialization.kotlinx.json)
                implementation(mystere.kotlinx.serialization.core)
                implementation(mystere.kotlinx.serialization.json)
            }
        }

        // jvm
        val jvmMain by getting {
            dependencies {
                implementation(mystere.slf4j.api)
                implementation(mystere.logback.classic)
                implementation(mystere.ktor.client.cio)
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
                implementation(mystere.ktor.client.cio)
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
                implementation(mystere.ktor.client.cio)
            }
        }

        // windows
        val mingwMain by getting {
            dependencies {
                implementation(mystere.ktor.client.winhttp)
            }
        }
    }
}

buildkonfig {
    packageName = findProperty("mystere.lib.util.pkgName")!!.toString()

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "VERSION_NAME", MYSTERE_LIB)
        buildConfigField(FieldSpec.Type.STRING, "COMMIT", GIT_HEAD)
    }
}
