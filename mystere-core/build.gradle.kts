import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(mystere.plugins.kotlin.multiplatform)
    alias(mystere.plugins.kotlin.plugin.serialization)
    alias(mystere.plugins.ksp)
    alias(mystere.plugins.ktorfit)
    alias(mystere.plugins.buildkonfig)
    alias(mystere.plugins.sqldelight)
}

kotlin {
    jvm {
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
        java {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
    listOf(
        macosArm64(),
        macosX64(),
    ).forEach {
        it.binaries.framework {
            baseName = "MystereCore"
        }
    }
    listOf(
        linuxArm64(),
        linuxX64(),
        mingwX64(),
    ).forEach {
        it.binaries.staticLib {
            baseName = "MystereCore"
        }
    }

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
                implementation(mystere.kotlinx.datetime)
                implementation(mystere.kotlinx.io.core)
            }
        }

        // jvm
        val jvmMain by getting {
            dependencies {
                implementation(mystere.ktor.client.cio)
                implementation(mystere.slf4j.api)
                implementation(mystere.logback.classic)
                implementation(mystere.sqldelight.driver.sqlite.jvm)
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
        val mingwMain by getting {
            dependencies {

            }
        }

        val nativeMain by getting {
            dependencies {
                implementation(mystere.sqldelight.driver.sqlite.native)
            }
        }
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
