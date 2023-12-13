package io.github.mystere.qq

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

actual val DBPostgre_Schema: SqlSchema<QueryResult.Value<Unit>>
    get() = throw NotImplementedError("Only jvm target support postgresql!")

actual fun DBPostgre(driver: SqlDriver): Transacter {
    throw NotImplementedError("Only jvm target support postgresql!")
}