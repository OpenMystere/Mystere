import com.codingfeline.buildkonfig.compiler.FieldSpec

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
                implementation(mystere.kotlinx.coroutines.core)
                implementation(mystere.ktor.client.core)

                implementation(project(":mystere-core"))
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
    packageName = findProperty("mystere.lib.onebot.api.pkgName")!!.toString()

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "VERSION_NAME", MYSTERE_LIB)
        buildConfigField(FieldSpec.Type.STRING, "COMMIT", GIT_HEAD)
    }
}
