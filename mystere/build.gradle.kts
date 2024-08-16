import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(mystere.plugins.kotlin.multiplatform)
    alias(mystere.plugins.kotlin.plugin.serialization)
    alias(mystere.plugins.buildkonfig)
    alias(mystere.plugins.ktor)
}

kotlin {
    jvm {
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
        mainRun {
            mainClass = "io.github.mystere.app.MystereKt"
        }
        java {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        application {
            mainClass = "io.github.mystere.app.MystereKt"
        }
    }
    listOf(
        macosArm64(),
        macosX64(),
        linuxArm64(),
        linuxX64(),
        // TODO: ktor-server
//        mingwX64(),
    ).forEach {
        it.binaries.executable {
            baseName = "mystere"
            entryPoint = "io.github.mystere.app.main"
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        // common
        val commonMain by getting {
            dependencies {
                implementation(mystere.kotlinx.coroutines.core)
                implementation(mystere.kotlinx.io.core)
                implementation(mystere.clikt)
                implementation(mystere.yamlkt)

                implementation(project(":mystere-core"))
                implementation(project(":mystere-qq"))

                implementation(project(":sdk-qq"))

                implementation(project(":onebot-api"))
                implementation(project(":onebot-v11"))
                implementation(project(":onebot-v12"))
            }
        }

        // jvm
        val jvmMain by getting {
            dependencies {

            }
        }

        // native
        val nativeMain by getting {
            dependencies {

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
//        val mingwMain by getting {
//            dependencies {
//
//            }
//        }
    }
}

// https://github.com/JetBrains/compose-multiplatform/issues/3123#issuecomment-1699296352
tasks.configureEach {
    if (name == "jvmRun" || name.contains("run(.*?)Executable".toRegex())) {
        (this as ProcessForkOptions).workingDir = project.file("bin")
    }
}

buildkonfig {
    packageName = findProperty("mystere.app.pkgName")!!.toString()

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "VERSION_NAME", MYSTERE_APP)
        buildConfigField(FieldSpec.Type.STRING, "COMMIT", GIT_HEAD)
    }
}
