import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.dantesys.Database
import data.dao.EntregasDAO
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

//https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-explore-composables.html
//https://cashapp.github.io/sqldelight/2.0.2/native_sqlite/
@Composable
@Preview
fun app(driver: SqlDriver) {
    val database = Database(driver)
    val entregas = EntregasDAO.selecionaInicio(database)
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Row(Modifier.fillMaxSize()){
            Column(Modifier.fillMaxWidth(0.2f),Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = {},Modifier.fillMaxWidth(0.9f)){
                    Text("Clientes")
                }
                Button(onClick = {},Modifier.fillMaxWidth(0.9f)){
                    Text("Nova Tabela")
                }
                Button(onClick = {},Modifier.fillMaxWidth(0.9f)){
                    Text("Ver Tabelas")
                }
            }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource("logo.png"),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(200.dp)
                )
                if(entregas.isEmpty()){
                    Text("No momento não tem entregas em aberto")
                }else{
                    Text("Ultimas entregas")
                    Row(Modifier.fillMaxWidth(0.75f)){
                        Text("Nome",Modifier.fillMaxWidth(0.5f).border(1.dp, Color.Black).padding(8.dp))
                        Text("Data",Modifier.fillMaxWidth(0.3f).border(1.dp, Color.Black).padding(8.dp))
                        Text("Entregas",Modifier.border(1.dp, Color.Black).padding(8.dp))
                    }
                    for (entrega in entregas){
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        val localDateTime = LocalDate.parse(entrega.data)
                        val qtd = EntregasDAO.contarClientes(database,entrega.id)
                        Row(Modifier.fillMaxWidth(0.5f)){
                            Text(entrega.nome,Modifier.fillMaxWidth(0.5f).border(1.dp, Color.Black).padding(8.dp))
                            Text(localDateTime.format(formatter).toString(),Modifier.fillMaxWidth(0.3f).border(1.dp, Color.Black).padding(8.dp))
                            Text(qtd.toString(),Modifier.border(1.dp, Color.Black).padding(8.dp))
                        }
                    }
                }
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
}

fun main() = application {
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:sistema.db",
        properties = Properties().apply { put("foreign_keys", "true")})
    Database.Schema.create(driver)
    Window(onCloseRequest = ::exitApplication,title = "Sistema de Tabelas") {
        app(driver)
    }
}
