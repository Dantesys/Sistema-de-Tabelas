package telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cash.sqldelight.db.SqlDriver
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dantesys.Database
import com.dantesys.sistemadetabelas.generated.resources.Res
import com.dantesys.sistemadetabelas.generated.resources.logo
import data.Entregas
import models.VTabelaScreenModel
import org.jetbrains.compose.resources.imageResource
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class VTabelaScreen(val driver: SqlDriver, val id:Long) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val db = Database(driver)
        val screenModel = rememberScreenModel { VTabelaScreenModel(db,id) }
        val state by screenModel.state.collectAsState()
        when (val result = state) {
            is VTabelaScreenModel.State.Loading -> LoadingContent(navigator)
            is VTabelaScreenModel.State.Result -> vertabelaScreen(result.entrega,navigator)
        }
    }
    @Composable
    fun LoadingContent(navigator: Navigator) {
        MaterialTheme {
            Row(Modifier.fillMaxSize()){
                Column(
                    Modifier.fillMaxWidth(0.2f).fillMaxHeight().background(Color(250,255,196)),
                    Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        bitmap = imageResource(Res.drawable.logo),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(200.dp)
                    )
                    Button(onClick = {navigator.popUntilRoot()}, Modifier.fillMaxWidth(0.9f)){
                        Text("Inicio")
                    }
                    Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                        Text("Novo Clientes")
                    }
                    Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                        Text("Ver Clientes")
                    }
                    Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                        Text("Nova Tabela")
                    }
                    Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                        Text("Ver Tabelas")
                    }
                }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
    @Composable
    fun vertabelaScreen(entrega: Entregas,navigator: Navigator) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDateTime = LocalDate.parse(entrega.data)
        MaterialTheme {
            Row(Modifier.fillMaxSize()){
                Column(
                    Modifier.fillMaxWidth(0.2f).fillMaxHeight().background(Color(250,255,196)),
                    Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        bitmap = imageResource(Res.drawable.logo),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(200.dp)
                    )
                    Button(onClick = {navigator.popUntilRoot()}, Modifier.fillMaxWidth(0.9f)){
                        Text("Inicio")
                    }
                    Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                        Text("Novo Clientes")
                    }
                    Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                        Text("Ver Clientes")
                    }
                    Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                        Text("Nova Tabela")
                    }
                    Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                        Text("Ver Tabelas")
                    }
                }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(entrega.nome+" - "+localDateTime.format(formatter).toString())
                    Row(Modifier.fillMaxWidth(0.75f).border(1.dp, Color.Black), Arrangement.SpaceAround){
                        Text("Entrega", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                        Text("Nome Fantasia", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                        Text("Cidade", Modifier.padding(8.dp))
                        Text("Bairro", Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}