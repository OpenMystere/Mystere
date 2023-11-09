plugins {
    alias(mystere.plugins.kotlin.multiplatform)
    alias(mystere.plugins.kotlin.plugin.serialization)
    alias(mystere.plugins.ksp)
    alias(mystere.plugins.ktorfit)
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
                implementation(mystere.kotlin.logging)

                implementation(mystere.ktor.client.core)
                implementation(mystere.ktor.client.content.negotiation)
                implementation(mystere.ktor.client.auth)
                implementation(mystere.ktor.plugin.logging)
                implementation(mystere.ktor.plugin.serialization.kotlinx.json)
                implementation(mystere.ktorfit.lib.light)

                implementation(mystere.kotlinx.coroutines.core)

                implementation(project(":mystere-core"))
            }
        }

        // jvm
        val jvmMain by getting {
            dependencies {
                implementation(mystere.ktor.client.cio)
                implementation(mystere.slf4j.api)
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
//        val linuxArm64Main by getting {
//            dependencies {
//
//            }
//        }
        val linuxMain by getting {
            dependencies {
                implementation(mystere.ktor.client.cio)
            }
        }

        // windows
        val mingwX64Main by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(mystere.ktor.client.winhttp)
            }
        }
    }
}

dependencies {
    with(mystere.ktorfit.ksp) {
        add("kspCommonMainMetadata", this)
        add("kspJvm", this)
//        add("kspLinuxArm64", this)
        add("kspLinuxX64", this)
        add("kspMacosArm64", this)
        add("kspMacosX64", this)
        add("kspMacosArm64", this)
        add("kspMingwX64", this)
    }
}

buildkonfig {
    packageName = findProperty("mystere.lib.qq.pkgName")!!.toString()

    defaultConfigs {

    }
}
