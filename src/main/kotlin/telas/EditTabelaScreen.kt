package telas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
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
import com.dantesys.Entrega
import models.EditTabelaScreenModel
import telas.parts.loadingContent
import telas.parts.menu
import util.toBrazilianDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditTabelaScreen(val id:Long) : Screen {

    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { EditTabelaScreenModel() }
        val state by screenModel.state.collectAsState()
        when (val result = state) {
            is EditTabelaScreenModel.State.Loading -> loadingContent(navigator)
            is EditTabelaScreenModel.State.Result -> edittabelaScreen(result.entrega,result.clientes,navigator,screenModel)
        }
        LaunchedEffect(currentCompositeKeyHash){
            screenModel.getClientes(id)
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun edittabelaScreen(entrega: Entrega, clientes:List<Cliente>, navigator: Navigator, screenModel:EditTabelaScreenModel) {
        val dialogState = remember { mutableStateOf(false) }
        val editState = remember { mutableStateOf(false) }
        val deleteState = remember { mutableStateOf(false) }
        val cNome = remember { mutableStateOf("") }
        val cCidade = remember { mutableStateOf("") }
        val cBairro = remember { mutableStateOf("") }
        val cCodigo = remember { mutableLongStateOf(0) }
        val nome = remember { mutableStateOf(entrega.nome) }
        val focusManager = LocalFocusManager.current
        var showDatePickerDialog by remember {mutableStateOf(false)}
        val datePickerState = rememberDatePickerState()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDateTime = LocalDate.parse(entrega.data_)
        val data = remember { mutableStateOf(localDateTime.format(formatter)) }
        val pos = remember { mutableLongStateOf(0) }
        val cant = remember { mutableLongStateOf(0) }
        Box(Modifier.fillMaxSize()){
            if (showDatePickerDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDatePickerDialog = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                datePickerState
                                    .selectedDateMillis?.let { millis ->
                                        data.value = millis.toBrazilianDateFormat()
                                    }
                                showDatePickerDialog = false
                            }) {
                            Text(text = "Escolher data")
                        }
                    }) {
                    DatePicker(state = datePickerState)
                }
            }
            Row(Modifier.fillMaxSize()){
                menu(Modifier.fillMaxWidth(0.2f).fillMaxHeight().background(Color(250,255,196)),
                    Arrangement.spacedBy(10.dp),
                    Alignment.CenterHorizontally,
                    navigator,
                    false
                )
                Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally){
                    Row(Modifier.fillMaxWidth(0.8f),Arrangement.SpaceAround, Alignment.CenterVertically){
                        Text("Entrega Nº"+entrega.id.toString(), style = TextStyle(fontSize = 30.sp))
                    }
                    Row(Modifier.fillMaxWidth(0.8f),Arrangement.SpaceAround, Alignment.CenterVertically){
                        Button(onClick = {dialogState.value = salvar(Entrega(entrega.id,nome.value,localDateTime.format(formatter2),entrega.pedencia),clientes,screenModel)},
                            border = BorderStroke(2.dp,Color.Black),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(232,232,232),contentColor = Color.Black)){
                            Text("Salvar")
                            Icon(imageVector =  Icons.Default.Save,"icone de salvar")
                        }
                    }
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceAround,Alignment.CenterVertically){
                        OutlinedTextField(nome.value,{nome.value = it},label = {Text("Nome da entrega")})
                        OutlinedTextField(data.value,{}, modifier = Modifier.onFocusEvent {
                            if (it.isFocused) {
                                showDatePickerDialog = true
                                focusManager.clearFocus(force = true)
                            }
                        },label = {Text("Data de Saída")},readOnly = true)
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
                                        Text("Bairro", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text("Editar", Modifier.padding(8.dp))
                                    }
                                }
                                var cor: Color
                                itemsIndexed(clientes){i,cliente ->
                                    var p = true
                                    if(cliente.nome != "" && cliente.cidade != "" && cliente.bairro != ""){
                                        p=false
                                    }
                                    val num = i+1
                                    cor = if(num%2==0){
                                        Color.LightGray
                                    }else{
                                        Color.White
                                    }
                                    Row(Modifier.fillMaxWidth().background(cor)){
                                        Text("$num°", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text(cliente.codigo.toString(), Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text(cliente.nome.toString().uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text(cliente.cidade.toString().uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text(cliente.bairro.toString().uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        if(p){
                                            IconButton(onClick = {cCodigo.value=cliente.codigo;cant.value=cliente.codigo;cNome.value=cliente.nome.toString();cCidade.value= cliente.cidade.toString();cBairro.value=cliente.bairro.toString();pos.value=num.toLong();editState.value=true}){
                                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                                    Icon(imageVector =  Icons.Default.Edit,"icone de editar")
                                                }
                                            }
                                            IconButton(onClick = {cCodigo.value=cliente.codigo;pos.value=num.toLong();deleteState.value=true}){
                                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                                    Icon(imageVector =  Icons.Default.Delete,"icone de excluir")
                                                }
                                            }
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
            if (dialogState.value) {
                AlertDialog(onDismissRequest = {dialogState.value = false},
                    title = {Text("AVISO", style = TextStyle(fontSize = 30.sp))},
                    text = {Text("Salvo com Sucesso", style = TextStyle(fontSize = 20.sp))},
                    confirmButton = {
                        Button(onClick = {dialogState.value = false;navigator.popUntil { it==ViewTabelaScreen::class }},
                            border = BorderStroke(2.dp,Color.Green),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(204,255,204),contentColor = Color.Green)
                        ){
                            Text("OK")
                        }
                    }
                )
            }
            if(editState.value){
                AlertDialog(onDismissRequest = {editState.value = false},
                    title = {Text("EDITAR", style = TextStyle(fontSize = 30.sp))},
                    text = {
                        Column{
                            OutlinedTextField(cCodigo.value.toString(),{cCodigo.value = it.toLongOrNull()?: 0},label = {Text("Código")})
                            OutlinedTextField(cNome.value,{cNome.value = it},label = {Text("Nome Fantasia")})
                            OutlinedTextField(cCidade.value,{cCidade.value = it},label = {Text("Cidade")})
                            OutlinedTextField(cBairro.value,{cBairro.value = it},label = {Text("Bairro")})
                        }
                    },
                    dismissButton = {
                        Button(onClick = {editState.value = false},
                            border = BorderStroke(2.dp,Color.Red),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(255,204,204),contentColor = Color.Red)
                        ) {
                        Text("Cancelar")
                        }
                    },
                    confirmButton = {
                        Button(onClick = {screenModel.editCliente(Cliente(cCodigo.value,cNome.value,cCidade.value,cBairro.value),entrega.id,pos.value,cant.value);editState.value = false;navigator.push(EditTabelaScreen(id))},
                            border = BorderStroke(2.dp,Color.Green),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(204,255,204),contentColor = Color.Green)
                        ) {
                        Text("Salvar")
                        }
                    }
                )
            }
            if(deleteState.value){
                AlertDialog(onDismissRequest = {deleteState.value = false},
                    title = {Text("EXCLUIR", style = TextStyle(fontSize = 30.sp))},
                    text = {
                        Column{
                            Text("Tem certeza que deseja excluir o cliente!", style = TextStyle(fontSize = 20.sp))
                        }
                    },
                    dismissButton = {
                        Button(onClick = {deleteState.value = false},
                            border = BorderStroke(2.dp,Color.Red),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(255,204,204),contentColor = Color.Red)
                        ) {
                            Text("Não")
                        }
                    },
                    confirmButton = {
                        Button(onClick = {screenModel.removerCliente(cCodigo.value,pos.value,entrega.id);deleteState.value = false;navigator.push(EditTabelaScreen(id))},
                            border = BorderStroke(2.dp,Color.Green),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(204,255,204),contentColor = Color.Green)
                        ) {
                            Text("Sim")
                        }
                    }
                )
            }
        }
    }
    private fun salvar(entrega: Entrega,clientes:List<Cliente>, screenModel: EditTabelaScreenModel):Boolean{
        screenModel.editEntrega(entrega,clientes)
        return true
    }
}