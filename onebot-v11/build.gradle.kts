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
    // TODO: ktor-server
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
                implementation(mystere.ktor.server.core)
                implementation(mystere.ktor.server.cio)
                implementation(mystere.ktor.server.websockets)
                implementation(mystere.ktor.plugin.logging)
                implementation(mystere.ktor.plugin.serialization.kotlinx.json)
                implementation(mystere.ktorfit.lib.light)

                implementation(mystere.kotlinx.coroutines.core)
                implementation(mystere.kotlinx.serialization.json)
                implementation(mystere.kotlinx.datetime)

                implementation(project(":onebot-api"))
                implementation(project(":mystere-core"))
                implementation(project(":kotlinx-serialization-cqcode"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(mystere.kotlin.test)
                implementation(mystere.ktor.server.tests)
            }
        }

        // jvm
        val jvmMain by getting {
            dependencies {
                implementation(mystere.ktor.client.cio)
//                implementation(mystere.ktor.server.cio)
            }
        }

        // macos
        val macosMain by getting {
            dependencies {
                implementation(mystere.ktor.client.cio)
//                implementation(mystere.ktor.server.cio)
            }
        }

        // linux
        val linuxMain by getting {
            dependencies {
                implementation(mystere.ktor.client.cio)
//                implementation(mystere.ktor.server.cio)
            }
        }

        // windows
//        val mingwMain by getting {
//            dependencies {
//                implementation(mystere.ktor.client.winhttp)
//                implementation(mystere.ktor.server.cio)
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
