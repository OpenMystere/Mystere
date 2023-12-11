package io.github.mystere.sqlite

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

actual fun createSqliteDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    config: MystereDatabaseConfig.SqliteDetail,
    name: String
): SqlDriver {
    TODO("Not yet implemented")
}

actual fun createMysqlDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    config: MystereDatabaseConfig.MySQLDetail,
    name: String
): SqlDriver {
    TODO("Not yet implemented")
}

actual fun createPostgreJvmDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    config: MystereDatabaseConfig.PostgreDetail,
    name: String
): SqlDriver {
    TODO("Not yet implemented")
}

actual fun createPostgreNativeDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    config: MystereDatabaseConfig.PostgreDetail,
    name: String
): SqlDriver {
    TODO("Not yet implemented")
}