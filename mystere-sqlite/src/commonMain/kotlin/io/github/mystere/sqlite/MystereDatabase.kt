package io.github.mystere.sqlite

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import io.github.mystere.core.Platform
import io.github.mystere.core.PlatformType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface MystereDatabaseConfig {
    @Serializable
    data class SqliteDetail(
        @SerialName("path")
        val path: String
    ): MystereDatabaseConfig
    @Serializable
    data class MySQLDetail(
        @SerialName("path")
        val path: String
    ): MystereDatabaseConfig
    @Serializable
    data class PostgreDetail(
        @SerialName("path")
        val path: String
    ): MystereDatabaseConfig

    companion object {
        fun init(config: MystereDatabaseConfig) {
            Config = config
        }
    }
}

private var Config: MystereDatabaseConfig? = null

interface MultiSqlSchema {
    val Sqlite: SqlSchema<QueryResult.Value<Unit>>
    val MySQL: SqlSchema<QueryResult.Value<Unit>>
    val PostgreJvm: SqlSchema<QueryResult.Value<Unit>>
    val PostgreNative: SqlSchema<QueryResult.Value<Unit>>
}

fun SqldelightDriver(
    schema: MultiSqlSchema,
    name: String,
): SqlDriver {
    Config?.let {
        return when (it) {
            is MystereDatabaseConfig.SqliteDetail -> createSqliteDriver(schema.Sqlite, it, name)
            is MystereDatabaseConfig.MySQLDetail -> createMysqlDriver(schema.MySQL, it, name)
            is MystereDatabaseConfig.PostgreDetail -> when (Platform) {
                PlatformType.JVM -> createPostgreJvmDriver(schema.PostgreJvm, it, name)
                else -> createPostgreNativeDriver(schema.PostgreNative, it, name)
            }
        }
    } ?: throw IllegalStateException("You should call MystereDatabaseConfig.init(config: MystereDatabaseConfig) first!")
}

expect fun createSqliteDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    config: MystereDatabaseConfig.SqliteDetail,
    name: String,
): SqlDriver

expect fun createMysqlDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    config: MystereDatabaseConfig.MySQLDetail,
    name: String,
): SqlDriver

expect fun createPostgreJvmDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    config: MystereDatabaseConfig.PostgreDetail,
    name: String,
): SqlDriver

expect fun createPostgreNativeDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    config: MystereDatabaseConfig.PostgreDetail,
    name: String,
): SqlDriver
