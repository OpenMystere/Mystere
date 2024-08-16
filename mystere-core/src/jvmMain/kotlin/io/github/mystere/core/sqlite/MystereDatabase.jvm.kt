package io.github.mystere.core.sqlite

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual fun createSqliteDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    name: String,
): SqlDriver {
    return JdbcSqliteDriver("jdbc:sqlite:$_sqliteBasePath/$name")
}