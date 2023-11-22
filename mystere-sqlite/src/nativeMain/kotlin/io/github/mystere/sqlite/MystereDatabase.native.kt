package io.github.mystere.sqlite

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual fun createSqliteDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    config: MystereDatabaseConfig.SqliteDetail,
    name: String,
): SqlDriver {
    return NativeSqliteDriver(schema, "${config.path}/$name")
}