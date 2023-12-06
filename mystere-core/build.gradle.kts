import com.codingfeline.buildkonfig.compiler.FieldSpec

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
    linuxArm64()
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

                implementation(mystere.ktor.client.core)
                implementation(mystere.ktor.client.content.negotiation)
                implementation(mystere.ktor.client.auth)
                implementation(mystere.ktor.plugin.logging)
                implementation(mystere.ktor.plugin.serialization.kotlinx.json)
                implementation(mystere.ktorfit.lib.light)

                implementation(mystere.kotlinx.coroutines.core)
            }
        }

        // jvm
        val jvmMain by getting {
            dependencies {
                implementation(mystere.ktor.client.cio)
                implementation(mystere.slf4j.api)
                implementation(mystere.logback.classic)
            }
        }

        // macos
        val macosMain by getting {
            dependencies {
                implementation(mystere.ktor.client.cio)
            }
        }

        // linux
        val linuxMain by getting {
            dependencies {
                implementation(mystere.ktor.client.cio)
            }
        }

        // windows
//        val mingwMain by getting {
//            dependsOn(commonMain)
//            dependencies {
//                implementation(mystere.ktor.client.winhttp)
////                implementation(mystere.ktor.server.cio)
//            }
//        }
    }
}

dependencies {
    with(mystere.ktorfit.ksp) {
        add("kspCommonMainMetadata", this)
        add("kspJvm", this)
        add("kspLinuxArm64", this)
        add("kspLinuxX64", this)
        add("kspMacosArm64", this)
        add("kspMacosX64", this)
        add("kspMacosArm64", this)
        add("kspMingwX64", this)
    }
}

buildkonfig {
    packageName = findProperty("mystere.lib.core.pkgName")!!.toString()

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "VERSION_NAME", MYSTERE_LIB)
        buildConfigField(FieldSpec.Type.STRING, "COMMIT", GIT_HEAD)
    }
}
