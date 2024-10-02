import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import cafe.adriel.voyager.navigator.Navigator
import com.dantesys.Database
import telas.InicioScreen
import java.util.*

//https://cashapp.github.io/sqldelight/2.0.2/native_sqlite/
fun main() = application {
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:sistema.db",
        properties = Properties().apply { put("foreign_keys", "true")})
    Database.Schema.create(driver)
    Window(onCloseRequest = ::exitApplication,title = "Sistema de Tabelas") {
        Navigator(InicioScreen(driver))
    }
}
