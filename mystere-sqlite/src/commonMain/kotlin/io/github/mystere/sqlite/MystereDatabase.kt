package io.github.mystere.sqlite

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface MystereDatabaseConfig {
    @Serializable
    data class SqliteDetail(
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

fun SqliteDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    name: String,
): SqlDriver {
    Config?.let {
        return when (it) {
            is MystereDatabaseConfig.SqliteDetail -> createSqliteDriver(schema, it, name)
        }
    } ?: throw IllegalStateException("You should call MystereDatabaseConfig.init(config: MystereDatabaseConfig) first!")
}

expect fun createSqliteDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    config: MystereDatabaseConfig.SqliteDetail,
    name: String,
): SqlDriver
