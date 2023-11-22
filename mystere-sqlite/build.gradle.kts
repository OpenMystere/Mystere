import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(mystere.plugins.kotlin.multiplatform)
    alias(mystere.plugins.buildkonfig)
    alias(mystere.plugins.sqldelight)
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
    macosArm64()
    macosX64()
    // TODO: sqldelight
//    linuxArm64()
    linuxX64()
    mingwX64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        // common
        val commonMain by getting {
            dependencies {
                api(mystere.kotlin.logging)
                implementation(mystere.kotlin.reflect)
                implementation(mystere.kotlin.stdlib)

                implementation(mystere.kotlinx.coroutines.core)
                implementation(mystere.kotlinx.serialization.core)
                implementation(mystere.sqldelight.extensions.coroutines)

                implementation(project(":mystere-core"))
            }
        }

        // jvm
        val jvmMain by getting {
            dependencies {
                implementation(mystere.sqldelight.driver.sqlite.jvm)
            }
        }

        // macos
        val macosMain by getting {
            dependencies {

            }
        }

        // linux
        val linuxMain by getting {
            dependencies {

            }
        }

        // windows
        val mingwX64Main by getting {
            dependsOn(commonMain)
            dependencies {

            }
        }

        // native
        val nativeMain by getting {
            dependencies {
                implementation(mystere.sqldelight.driver.sqlite.native)
            }
        }
    }
}

buildkonfig {
    packageName = findProperty("mystere.lib.sqlite.pkgName")!!.toString()

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "VERSION_NAME", MYSTERE_LIB)
        buildConfigField(FieldSpec.Type.STRING, "COMMIT", GIT_HEAD)
    }
}
