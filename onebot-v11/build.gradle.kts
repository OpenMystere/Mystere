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
//    mingwX64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        // common
        val commonMain by getting {
            dependencies {
                implementation(mystere.kotlin.reflect)
                implementation(mystere.kotlin.stdlib)

                implementation(mystere.ktor.client.core)
                implementation(mystere.ktor.client.content.negotiation)
                implementation(mystere.ktor.client.auth)
                implementation(mystere.ktor.plugin.logging)
                implementation(mystere.ktor.plugin.serialization.kotlinx.json)
                implementation(mystere.ktorfit.lib.light)

                implementation(mystere.kotlinx.coroutines.core)
                implementation(mystere.kotlinx.serialization.json)
                implementation(mystere.kotlinx.datetime)

                implementation(project(":onebot-api"))
                implementation(project(":mystere-util"))
                implementation(project(":kotlinx-serialization-cqcode"))
            }
        }

        // jvm
        val jvmMain by getting {
            dependencies {
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
//        val mingwX64Main by getting {
//            dependencies {
//                implementation(mystere.ktor.client.winhttp)
//            }
//        }
    }
}

buildkonfig {
    packageName = findProperty("mystere.lib.onebot.v11.pkgName")!!.toString()

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "VERSION_NAME", MYSTERE_LIB)
        buildConfigField(FieldSpec.Type.STRING, "COMMIT", GIT_HEAD)
    }
}
