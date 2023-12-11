import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(mystere.plugins.kotlin.multiplatform)
    alias(mystere.plugins.kotlin.plugin.serialization)
    alias(mystere.plugins.buildkonfig)
    alias(mystere.plugins.sqldelight)
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
                implementation(mystere.kotlin.logging)

                implementation(mystere.ktor.client.core)
                implementation(mystere.ktor.client.content.negotiation)
                implementation(mystere.ktor.client.auth)
                implementation(mystere.ktor.plugin.serialization.kotlinx.json)

                implementation(mystere.kotlinx.coroutines.core)
                implementation(mystere.kotlinx.io.core)

                implementation(project(":kotlinx-serialization-cqcode"))

                implementation(project(":mystere-core"))
                implementation(project(":mystere-sqlite"))

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

sqldelight {
    databases {
        val pkgName = findProperty("mystere.lib.qq.pkgName")!!.toString()
        val DBSqlite by creating {
            packageName = pkgName
            deriveSchemaFromMigrations = true
            srcDirs(project.file("./src/commonMain/sqldelight-sqlite"))
        }
        val DBMySQL by creating {
            packageName = pkgName
            deriveSchemaFromMigrations = true
            srcDirs(project.file("./src/commonMain/sqldelight-mysql"))
            dialect(mystere.sqldelight.dialect.mysql)
        }
        val DBPostgreJVM by creating {
            packageName = pkgName
            deriveSchemaFromMigrations = true
            srcDirs(project.file("./src/commonMain/sqldelight-mysql"))
            dialect(mystere.sqldelight.dialect.postgresql.jvm)
        }
        val DBPostgreNative by creating {
            packageName = pkgName
            deriveSchemaFromMigrations = true
            srcDirs(project.file("./src/commonMain/sqldelight-mysql"))
            dialect(mystere.sqldelight.dialect.postgresql.native)
        }
    }
}


// https://github.com/JetBrains/compose-multiplatform/issues/3123#issuecomment-1699296352
tasks.configureEach {
    if (name == "jvmRun" || name.contains("run(.*?)Executable".toRegex())) {
        (this as ProcessForkOptions).workingDir = project.file("bin")
    }
}

buildkonfig {
    packageName = findProperty("mystere.lib.qq.pkgName")!!.toString()

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "VERSION_NAME", MYSTERE_APP)
        buildConfigField(FieldSpec.Type.STRING, "COMMIT", GIT_HEAD)
    }
}
