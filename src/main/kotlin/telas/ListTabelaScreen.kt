package telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dantesys.Entrega
import models.ListTabelaScreenModel
import telas.parts.loadingContent
import telas.parts.menu
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ListTabelaScreen:Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ListTabelaScreenModel() }
        val state by screenModel.state.collectAsState()
        when (val result = state) {
            is ListTabelaScreenModel.State.Loading -> loadingContent(navigator)
            is ListTabelaScreenModel.State.Result -> list(result.entregas,result.clientes,result.pages,navigator,screenModel)
        }
        LaunchedEffect(currentCompositeKeyHash){
            screenModel.getEntregas()
        }
    }
    @Composable
    fun list(entregas:List<Entrega>,clientes:List<Long>,pages:Long,navigator: Navigator,screenModel: ListTabelaScreenModel){
        val filtro = remember { mutableStateOf("") }
        val page = remember { mutableLongStateOf(1L)}
        Box(Modifier.fillMaxSize()){
            Row(Modifier.fillMaxSize()){
                menu(Modifier.fillMaxWidth(0.2f).fillMaxHeight().background(Color(250,255,196)),
                    Arrangement.spacedBy(10.dp),
                    Alignment.CenterHorizontally,
                    navigator,
                    false,
                    vtabela = true
                )
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                    Row(Modifier.fillMaxWidth().padding(10.dp),Arrangement.Center,verticalAlignment = Alignment.CenterVertically){
                        OutlinedTextField(filtro.value,{filtro.value = it},label = {Text("Buscar")},singleLine=true,
                            trailingIcon = {
                                IconButton(onClick = {screenModel.getEntregas(filtro.value,page.value.toInt())}){
                                    Icon(imageVector =  Icons.Default.Search,"icone de busca",modifier = Modifier.size(20.dp))
                                }
                            }
                        )
                    }
                    Row(Modifier.fillMaxWidth().padding(10.dp),Arrangement.Center,verticalAlignment = Alignment.CenterVertically){
                        if(page.value>1L){
                            IconButton(onClick = {page.value--;screenModel.getEntregas(filtro.value,page.value.toInt())}){
                                Icon(imageVector =  Icons.Default.KeyboardDoubleArrowLeft,"icone de voltar")
                            }
                        }
                        Text("Página ${page.value} de $pages")
                        if(pages>1L && page.value<pages){
                            IconButton(onClick = {page.value++;screenModel.getEntregas(filtro.value,page.value.toInt())}){
                                Icon(imageVector =  Icons.Default.KeyboardDoubleArrowRight,"icone de final")
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(0.75f).border(1.dp, Color.Black), Arrangement.SpaceAround){
                        Text("N°", Modifier.fillMaxWidth(0.1f).padding(8.dp))
                        Text("Nome", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                        Text("Data", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                        Text("Entregas", Modifier.padding(8.dp))
                        Text("Detalhar", Modifier.padding(8.dp))
                    }
                    for (entrega in entregas){
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        val localDateTime = LocalDate.parse(entrega.data_)
                        val qtd = clientes[entregas.indexOf(entrega)]
                        Row(Modifier.fillMaxWidth(0.75f).border(1.dp, Color.Black), Arrangement.SpaceAround){
                            Text(entrega.id.toString(), Modifier.fillMaxWidth(0.1f).padding(8.dp))
                            Text(entrega.nome.uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                            Text(localDateTime.format(formatter).toString(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                            Text(qtd.toString(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                            IconButton(onClick = {navigator.push(ViewTabelaScreen(entrega.id))}){
                                if(entrega.pedencia<1L){
                                    Icon(imageVector =  Icons.Default.Search,"icone de informação")
                                }else{
                                    Icon(imageVector =  Icons.Default.Search,"icone de informação",tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}