package telas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
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
import com.dantesys.Cliente
import models.ListClienteScreenModel
import telas.parts.loadingContent
import telas.parts.menu

class ListClienteScreen: Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ListClienteScreenModel() }
        val state by screenModel.state.collectAsState()
        when (val result = state) {
            is ListClienteScreenModel.State.Loading -> loadingContent(navigator)
            is ListClienteScreenModel.State.Result -> list(result.clientes,result.pages,navigator,screenModel)
        }
        LaunchedEffect(currentCompositeKeyHash){
            screenModel.getClientes()
        }
    }
    @Composable
    fun list(clientes:List<Cliente>, pages:Long, navigator: Navigator, screenModel: ListClienteScreenModel){
        val filtro = remember { mutableStateOf("") }
        val page = remember { mutableLongStateOf(1L) }
        val cod = remember { mutableStateOf(false) }
        val nom = remember { mutableStateOf(false) }
        val cid = remember { mutableStateOf(false) }
        val bai = remember { mutableStateOf(false) }
        val editState = remember { mutableStateOf(false) }
        val cNome = remember { mutableStateOf("") }
        val cCidade = remember { mutableStateOf("") }
        val cBairro = remember { mutableStateOf("") }
        val cCodigo = remember { mutableLongStateOf(0) }
        Box(Modifier.fillMaxSize()){
            Row(Modifier.fillMaxSize()){
                menu(
                    Modifier.fillMaxWidth(0.2f).fillMaxHeight().background(Color(250,255,196)),
                    Arrangement.spacedBy(10.dp),
                    Alignment.CenterHorizontally,
                    navigator,
                    false,
                    vcliente = true
                )
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                    Row(Modifier.fillMaxWidth().padding(10.dp),Arrangement.Center,verticalAlignment = Alignment.CenterVertically){
                        OutlinedTextField(filtro.value,{filtro.value = it},label = { Text("Buscar") },singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = {page.value=1L;screenModel.getClientes(filtro.value,page.value.toInt(),cod.value,nom.value,cid.value,bai.value)}){
                                    Icon(imageVector =  Icons.Default.Search,"icone de busca",modifier = Modifier.size(20.dp))
                                }
                            },modifier = Modifier.onKeyEvent{
                                if(it.type == KeyEventType.KeyUp && (it.key == Key.Enter) || (it.key==Key.NumPadEnter)){
                                    page.value=1L
                                    screenModel.getClientes(filtro.value,page.value.toInt(),cod.value,nom.value,cid.value,bai.value)
                                }
                                false
                            }
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        Text("Filtros",style = TextStyle(fontSize = 20.sp))
                        Spacer(modifier = Modifier.padding(10.dp))
                        Box(modifier = Modifier.border(1.dp, Color.Black).padding(5.dp)){
                            Row{
                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                    Row(horizontalArrangement = Arrangement.Center,verticalAlignment = Alignment.CenterVertically){
                                        Text("Código")
                                        Checkbox(cod.value,{cod.value=it})
                                    }
                                    Row(horizontalArrangement = Arrangement.Center,verticalAlignment = Alignment.CenterVertically){
                                        Text("Cidade")
                                        Checkbox(cid.value,{cid.value=it})
                                    }
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                    Row(horizontalArrangement = Arrangement.Center,verticalAlignment = Alignment.CenterVertically){
                                        Text("Nome")
                                        Checkbox(nom.value,{nom.value=it})
                                    }
                                    Row(horizontalArrangement = Arrangement.Center,verticalAlignment = Alignment.CenterVertically){
                                        Text("Bairro")
                                        Checkbox(bai.value,{bai.value=it})
                                    }
                                }
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth().padding(10.dp),Arrangement.Center,verticalAlignment = Alignment.CenterVertically){
                        if(page.value>1L){
                            IconButton(onClick = {page.value--;screenModel.getClientes(filtro.value,page.value.toInt(),cod.value,nom.value,cid.value,bai.value)}){
                                Icon(imageVector =  Icons.Default.KeyboardDoubleArrowLeft,"icone de voltar")
                            }
                        }
                        Text("Página ${page.value} de $pages")
                        if(pages>1L && page.value<pages){
                            IconButton(onClick = {page.value++;screenModel.getClientes(filtro.value,page.value.toInt(),cod.value,nom.value,cid.value,bai.value)}){
                                Icon(imageVector =  Icons.Default.KeyboardDoubleArrowRight,"icone de final")
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceAround, Alignment.CenterVertically){
                        Box(Modifier.fillMaxSize().padding(50.dp).align(Alignment.CenterVertically)) {
                            val state = rememberLazyListState()
                            LazyColumn(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,state = state) {
                                item{
                                    Row(Modifier.fillMaxWidth().border(1.dp, Color.Black), Arrangement.SpaceAround){
                                        Text("Código", Modifier.fillMaxWidth(0.1f).padding(8.dp))
                                        Text("Nome", Modifier.fillMaxWidth(0.3f).padding(8.dp))
                                        Text("Cidade", Modifier.fillMaxWidth(0.2f).padding(8.dp))
                                        Text("Bairro", Modifier.fillMaxWidth(0.3f).padding(8.dp))
                                        Text("Editar", Modifier.padding(8.dp))
                                    }
                                }
                                itemsIndexed(clientes){ _, cliente ->
                                    Row(Modifier.fillMaxWidth().border(1.dp, Color.Black), Arrangement.SpaceAround){
                                        Text(cliente.codigo.toString(), Modifier.fillMaxWidth(0.1f).padding(8.dp))
                                        if(cliente.nome!=null){
                                            Text(cliente.nome.uppercase(), Modifier.fillMaxWidth(0.3f).padding(8.dp))
                                        }else{
                                            Text("", Modifier.fillMaxWidth(0.3f).padding(8.dp))
                                        }
                                        if(cliente.cidade!=null){
                                            Text(cliente.cidade.uppercase(), Modifier.fillMaxWidth(0.2f).padding(8.dp))
                                        }else{
                                            Text("", Modifier.fillMaxWidth(0.2f).padding(8.dp))
                                        }
                                        if(cliente.bairro!=null){
                                            Text(cliente.bairro.uppercase(), Modifier.fillMaxWidth(0.3f).padding(8.dp))
                                        }else{
                                            Text("", Modifier.fillMaxWidth(0.3f).padding(8.dp))
                                        }
                                        IconButton(onClick = {cCodigo.value=cliente.codigo;cNome.value=cliente.nome.toString();cCidade.value= cliente.cidade.toString();cBairro.value=cliente.bairro.toString();editState.value=true}){
                                            Icon(imageVector =  Icons.Default.Edit,"icone de informação")
                                        }
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
            if(editState.value){
                AlertDialog(onDismissRequest = {editState.value = false},
                    title = {Text("EDITAR")},
                    text = {
                        Column{
                            OutlinedTextField(cCodigo.value.toString(),{},label = {Text("Código")},readOnly = true,colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(51,51,0), focusedLabelColor = Color(51,51,0)))
                            OutlinedTextField(cNome.value,{cNome.value = it},label = {Text("Nome Fantasia")},colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(51,51,0), focusedLabelColor = Color(51,51,0)))
                            OutlinedTextField(cCidade.value,{cCidade.value = it},label = {Text("Cidade")},colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(51,51,0), focusedLabelColor = Color(51,51,0)))
                            OutlinedTextField(cBairro.value,{cBairro.value = it},label = {Text("Bairro")},colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(51,51,0), focusedLabelColor = Color(51,51,0)))
                        }
                    },
                    dismissButton = {
                        Button(onClick = {editState.value = false},
                            border = BorderStroke(2.dp,Color.Red),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(255,204,204),contentColor = Color.Red)) {
                            Text("Cancelar")
                        }
                    },
                    confirmButton = {
                        Button(onClick = {screenModel.editCliente(Cliente(cCodigo.value,cNome.value,cCidade.value,cBairro.value),filtro.value,page.value.toInt(),cod.value,nom.value,cid.value,bai.value);editState.value = false},
                            border = BorderStroke(2.dp,Color.Green),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(204,255,204),contentColor = Color.Green)) {
                            Text("Salvar")
                        }
                    }
                )
            }
        }
    }
}