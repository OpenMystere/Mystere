package io.github.mystere.core.sqlite

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual fun createSqliteDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    name: String
): SqlDriver {
    return NativeSqliteDriver(schema, "$_sqliteBasePath/$name")
}
