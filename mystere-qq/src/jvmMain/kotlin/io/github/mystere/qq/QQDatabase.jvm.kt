package io.github.mystere.qq

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import io.github.mystere.qq.database.DBPostgreJVM

actual val DBPostgre_Schema: SqlSchema<QueryResult.Value<Unit>> = DBPostgreJVM.Schema
actual fun DBPostgre(driver: SqlDriver): Transacter {
    return DBPostgreJVM(driver)
}