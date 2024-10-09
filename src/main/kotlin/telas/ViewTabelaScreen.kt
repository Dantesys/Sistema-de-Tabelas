package telas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
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
import models.ViewTabelaScreenModel
import telas.parts.menu
import util.gerarPDF
import util.imprimir
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ViewTabelaScreen(val id:Long) : Screen {

    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ViewTabelaScreenModel() }
        val state by screenModel.state.collectAsState()
        when (val result = state) {
            is ViewTabelaScreenModel.State.Loading -> loadingContent(navigator)
            is ViewTabelaScreenModel.State.Result -> vertabelaScreen(result.entrega,navigator)
        }
        LaunchedEffect(currentCompositeKeyHash){
            screenModel.getClientes(id)
        }
    }
    @Composable
    fun vertabelaScreen(entrega: Entregas,navigator: Navigator) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDateTime = LocalDate.parse(entrega.data)
        val dialogState = remember { mutableStateOf(false) }
        Box(Modifier.fillMaxSize()){
            Row(Modifier.fillMaxSize()){
                menu(Modifier.fillMaxWidth(0.2f).fillMaxHeight().background(Color(250,255,196)),
                    Arrangement.spacedBy(10.dp),
                    Alignment.CenterHorizontally,
                    navigator,
                    false
                )
                Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally){
                    Row(Modifier.fillMaxWidth(0.8f),Arrangement.SpaceAround, Alignment.CenterVertically){
                        IconButton(onClick = {imprimir(entrega)}){
                            Column(horizontalAlignment = Alignment.CenterHorizontally){
                                Text("Imprimir")
                                Icon(imageVector =  Icons.Default.Print,"icone de imprensão")
                            }
                        }
                        Text(entrega.nome.uppercase()+" - "+localDateTime.format(formatter).toString(), style = TextStyle(fontSize = 30.sp))
                        IconButton(onClick = {dialogState.value = gerarPDF(entrega)}){
                            Column(horizontalAlignment = Alignment.CenterHorizontally){
                                Text("Salvar PDF")
                                Icon(imageVector =  Icons.Default.PictureAsPdf,"icone de salvar")
                            }
                        }
                        if(entrega.pedencia){
                            IconButton(onClick = {navigator.push(EditTabelaScreen(id))}){
                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                    Text("Editar")
                                    Icon(imageVector =  Icons.Default.Edit,"icone de editar")
                                }
                            }
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
                                    cor = if(num%2==0){
                                        Color.LightGray
                                    }else{
                                        Color.White
                                    }
                                    Row(Modifier.fillMaxWidth().background(cor)){
                                        Text("$num°", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text(cliente.codigo.toString(), Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text(cliente.nome.uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text(cliente.cidade.uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text(cliente.bairro.uppercase(), Modifier.padding(8.dp))
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
            if (dialogState.value) {
                AlertDialog(onDismissRequest = {dialogState.value = false},
                    title = {Text("AVISO")},
                    text = {Text("PDF Salvo com Sucesso")},
                    confirmButton = {
                        Button(onClick = {dialogState.value = false}) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}