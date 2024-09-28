import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.cash.paging.PagingSource
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.paging3.QueryPagingSource
import com.dantesys.Database
import com.dantesys.EntregaQueries
import data.Entregas
import kotlinx.coroutines.Dispatchers
import java.util.*

//https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-explore-composables.html
//https://cashapp.github.io/sqldelight/2.0.2/native_sqlite/
@Composable
@Preview
fun app(driver: SqlDriver) {
    val database = Database(driver)
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) {
                Text("Diga Olá!")
            }
            AnimatedVisibility(showContent) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Olá Mundo!")
                }
            }
        }
    }
}

fun main() = application {
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:sistema.db",
        properties = Properties().apply { put("foreign_keys", "true")})
    Database.Schema.create(driver)
    Window(onCloseRequest = ::exitApplication,title = "Sistema de Tabelas") {
        app(driver)
    }
}
