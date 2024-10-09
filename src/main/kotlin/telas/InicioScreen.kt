package telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.Entregas
import telas.parts.loadingContent
import models.InicioScreenModel
import telas.parts.menu
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InicioScreen : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { InicioScreenModel() }
        val state by screenModel.state.collectAsState()
        when (val result = state) {
            is InicioScreenModel.State.Loading -> loadingContent(navigator)
            is InicioScreenModel.State.Result -> inicio(result.entregas,result.clientes,result.pedencias,result.clientesp,navigator)
        }
        LaunchedEffect(currentCompositeKeyHash){
            screenModel.getInicio()
        }
    }
    @Composable
    fun inicio(entregas: List<Entregas>, clientes: List<Long>, pedencias: List<Entregas>,clientesp: List<Long>,navigator: Navigator){
        MaterialTheme {
            Row(Modifier.fillMaxSize()){
                menu(Modifier.fillMaxWidth(0.2f).fillMaxHeight().background(Color(250,255,196)),Arrangement.spacedBy(10.dp),Alignment.CenterHorizontally,navigator)
                Column(Modifier.fillMaxWidth(),Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                    if(entregas.isEmpty()){
                        Text("No momento não tem entregas em aberto", style = TextStyle(fontSize = 30.sp))
                    }else{
                        Text("Ultimas entregas finalizadas", style = TextStyle(fontSize = 30.sp))
                        Row(Modifier.fillMaxWidth(0.75f).border(1.dp, Color.Black), Arrangement.SpaceAround){
                            Text("N°", Modifier.fillMaxWidth(0.1f).padding(8.dp))
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
                                Text(entrega.id.toString(), Modifier.fillMaxWidth(0.1f).padding(8.dp))
                                Text(entrega.nome.uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                Text(localDateTime.format(formatter).toString(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                Text(qtd.toString(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                IconButton(onClick = {navigator.push(ViewTabelaScreen(entrega.id))}){
                                    Icon(imageVector =  Icons.Default.Search,"icone de informação")
                                }
                            }
                        }
                    }
                    if(pedencias.isNotEmpty()){
                        Text("Ultimas entregas com Pendências", style = TextStyle(fontSize = 30.sp))
                        Row(Modifier.fillMaxWidth(0.75f).border(1.dp, Color.Black), Arrangement.SpaceAround){
                            Text("N°", Modifier.fillMaxWidth(0.1f).padding(8.dp))
                            Text("Nome", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                            Text("Data", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                            Text("Entregas", Modifier.padding(8.dp))
                            Text("Detalhar", Modifier.padding(8.dp))
                        }
                        for (entrega in pedencias){
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            val localDateTime = LocalDate.parse(entrega.data)
                            val qtd = clientesp[pedencias.indexOf(entrega)]
                            Row(Modifier.fillMaxWidth(0.75f).border(1.dp, Color.Black), Arrangement.SpaceAround){
                                Text(entrega.id.toString(), Modifier.fillMaxWidth(0.1f).padding(8.dp))
                                Text(entrega.nome.uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                Text(localDateTime.format(formatter).toString(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                Text(qtd.toString(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                IconButton(onClick = {navigator.push(ViewTabelaScreen(entrega.id))}){
                                    Icon(imageVector =  Icons.Default.Search,"icone de informação")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
