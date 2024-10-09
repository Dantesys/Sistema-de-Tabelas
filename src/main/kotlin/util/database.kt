package util

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.dantesys.Database
import java.util.*

fun getDB(): Database {
    return Database(getDriver())
}
fun getDriver(): SqlDriver {
    return JdbcSqliteDriver("jdbc:sqlite:sistema.db",properties = Properties().apply { put("foreign_keys", "true")})
}