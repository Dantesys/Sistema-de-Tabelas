package telas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import models.InicioScreenModel
import org.jetbrains.compose.resources.imageResource
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class InicioScreen(val driver: SqlDriver) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val db = Database(driver)
        val screenModel = rememberScreenModel { InicioScreenModel(db) }
        val state by screenModel.state.collectAsState()
        when (val result = state) {
            is InicioScreenModel.State.Loading -> LoadingContent()
            is InicioScreenModel.State.Result -> Inicio(result.entregas,result.clientes,navigator)
        }
    }
    @Composable
    fun LoadingContent() {
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
    fun Inicio(entregas: List<Entregas>, clientes: List<Long>, navigator: Navigator){
        MaterialTheme {
            var showContent by remember { mutableStateOf(false) }
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
                    if(entregas.isEmpty()){
                        Text("No momento não tem entregas em aberto")
                        Image(
                            bitmap = imageResource(Res.drawable.logo),
                            contentDescription = "Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(200.dp)
                        )
                    }else{
                        Text("Ultimas entregas")
                        Row(Modifier.fillMaxWidth(0.75f).border(1.dp, Color.Black), Arrangement.SpaceAround){
                            Text("Nome", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                            Text("Data", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                            Text("Entregas", Modifier.padding(8.dp))
                            Text("Detalhar", Modifier.padding(8.dp))
                        }
                        for (entrega in entregas){
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            val localDateTime = LocalDate.parse(entrega.data)
                            val qtd = clientes[entregas.indexOf(entrega)]
                            Row(Modifier.fillMaxWidth(0.75f).border(1.dp, Color.Black), Arrangement.SpaceAround){
                                Text(entrega.nome, Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                Text(localDateTime.format(formatter).toString(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                Text(qtd.toString(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                IconButton(onClick = {navigator.push(VTabelaScreen(driver,entrega.id))}){
                                    Icon(imageVector =  Icons.Default.Info,"icone de informação")
                                }
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
}
