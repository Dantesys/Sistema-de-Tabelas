package telas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Print
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
import util.imprimir
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
                        IconButton(onClick = {dialogState.value = imprimirSave(Entrega(entrega.id,nome.value,localDateTime.format(formatter2),entrega.pedencia),clientes,screenModel)}){
                            Column(horizontalAlignment = Alignment.CenterHorizontally){
                                Text("Salvar e imprimir")
                                Icon(imageVector =  Icons.Default.Print,"icone de imprensão")
                            }
                        }
                        Text("Entrega Nº"+entrega.id.toString(), style = TextStyle(fontSize = 30.sp))
                        IconButton(onClick = {dialogState.value = salvar(Entrega(entrega.id,nome.value,localDateTime.format(formatter2),entrega.pedencia),clientes,screenModel)}){
                            Column(horizontalAlignment = Alignment.CenterHorizontally){
                                Text("Salvar")
                                Icon(imageVector =  Icons.Default.Save,"icone de salvar")
                            }
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
                                            IconButton(onClick = {cCodigo.value=cliente.codigo;cNome.value=cliente.nome.toString();cCidade.value= cliente.cidade.toString();cBairro.value=cliente.bairro.toString();editState.value=true}){
                                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                                    Icon(imageVector =  Icons.Default.Edit,"icone de editar")
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
                    title = {Text("AVISO")},
                    text = {Text("Salvo com Sucesso")},
                    confirmButton = {
                        Button(onClick = {dialogState.value = false;navigator.popUntil { it==ViewTabelaScreen::class }}) {
                            Text("OK")
                        }
                    }
                )
            }
            if(editState.value){
                AlertDialog(onDismissRequest = {editState.value = false},
                    title = {Text("EDITAR")},
                    text = {
                        Column{
                            OutlinedTextField(cCodigo.value.toString(),{},label = {Text("Código")},readOnly = true)
                            OutlinedTextField(cNome.value,{cNome.value = it},label = {Text("Nome Fantasia")})
                            OutlinedTextField(cCidade.value,{cCidade.value = it},label = {Text("Cidade")})
                            OutlinedTextField(cBairro.value,{cBairro.value = it},label = {Text("Bairro")})
                        }
                    },
                    dismissButton = {
                        Button(onClick = {editState.value = false}) {
                            Text("Cancelar")
                        }
                    },
                    confirmButton = {
                        Button(onClick = {screenModel.editCliente(Cliente(cCodigo.value,cNome.value,cCidade.value,cBairro.value),entrega.id);editState.value = false;navigator.push(EditTabelaScreen(id))}) {
                            Text("Salvar")
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
    private fun imprimirSave(entrega: Entrega,clientes:List<Cliente>, screenModel: EditTabelaScreenModel):Boolean{
        if(salvar(entrega,clientes,screenModel)){
            imprimir(entrega,clientes)
        }
        return true
    }
}