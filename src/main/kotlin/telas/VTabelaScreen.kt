package telas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.sqldelight.db.SqlDriver
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
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

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val db = Database(driver)
        val screenModel = rememberScreenModel { VTabelaScreenModel() }
        val state by screenModel.state.collectAsState()
        when (val result = state) {
            is VTabelaScreenModel.State.Loading -> LoadingContent(navigator)
            is VTabelaScreenModel.State.Result -> vertabelaScreen(result.entrega,navigator)
        }
        LaunchedEffect(currentCompositeKeyHash){
            screenModel.getClientes(db,id)
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
        Box(Modifier.fillMaxSize()){
            Row(Modifier.fillMaxSize()){
                Column(Modifier.fillMaxWidth(0.2f).fillMaxHeight().background(Color(250,255,196)),
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
                Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally){
                    Row(Modifier.fillMaxWidth(0.8f),Arrangement.SpaceAround, Alignment.CenterVertically){
                        Button(onClick = {}, Modifier.fillMaxWidth(0.2f)){
                            Text("Imprimir")
                        }
                        Text(entrega.nome+" - "+localDateTime.format(formatter).toString(), style = TextStyle(fontSize = 30.sp))
                        Button(onClick = {}, Modifier.fillMaxWidth(0.2f)){
                            Text("Salvar PDF")
                        }
                    }
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceAround, Alignment.CenterVertically){
                        Box(Modifier.fillMaxSize().padding(50.dp).align(Alignment.CenterVertically)){
                            val state = rememberLazyListState()
                            LazyColumn(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,state = state) {
                                item {
                                    Row(Modifier.fillMaxWidth().border(1.dp, Color.Black)){
                                        Text("Entrega", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text("Código", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text("Nome Fantasia", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text("Cidade", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text("Bairro", Modifier.padding(8.dp))
                                    }
                                }
                                var cor: Color
                                itemsIndexed(entrega.clientes){i,cliente ->
                                    val num = i+1
                                    if(num%2==0){
                                        cor = Color.LightGray
                                    }else{
                                        cor = Color.White
                                    }
                                    Row(Modifier.fillMaxWidth().background(cor)){
                                        if(num<10){
                                            Text("0$num°", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        }else{
                                            Text("$num°", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        }
                                        Text(cliente.codigo.toString(), Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text(cliente.nome, Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text(cliente.cidade, Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text(cliente.bairro, Modifier.padding(8.dp))
                                    }
                                }
                            }
                            VerticalScrollbar(
                                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                adapter = rememberScrollbarAdapter(
                                    scrollState = state
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}