package io.github.mystere.core.sqlite

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

internal var _sqliteBasePath: String = "./data"
fun setSqliteBasePath(path: String) {
    _sqliteBasePath = if (path.endsWith("/")) {
        path.substring(0, path.length - 1)
    } else {
        path
    }
}

expect fun createSqliteDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    name: String,
): SqlDriver
