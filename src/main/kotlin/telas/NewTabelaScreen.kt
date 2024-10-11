package telas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import repository.data.Cliente
import repository.data.Entregas
import models.NewTabelaScreenModel
import telas.parts.menu
import util.imprimir
import util.toBrazilianDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NewTabelaScreen() : Screen {

    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { NewTabelaScreenModel() }
        novatabelaScreen(navigator,screenModel)
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun novatabelaScreen(navigator: Navigator, screenModel: NewTabelaScreenModel) {
        val nome = remember {mutableStateOf("")}
        val data = remember {mutableStateOf("")}
        val numero = remember {mutableLongStateOf(0)}
        val dialogState = remember {mutableStateOf(false)}
        val deleteState = remember {mutableStateOf(false)}
        val clientecodigo = remember {mutableLongStateOf(0)}
        val clientes = remember {mutableListOf<Cliente>()}
        val clienteindex = remember {mutableIntStateOf(0)}
        val focusManager = LocalFocusManager.current
        var showDatePickerDialog by remember {mutableStateOf(false)}
        val datePickerState = rememberDatePickerState()
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
                    false,
                    ntabela = true
                )
                Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally){
                    Row(Modifier.fillMaxWidth(0.8f),Arrangement.SpaceAround, Alignment.CenterVertically){
                        IconButton(onClick = {dialogState.value = imprimirSave(Entregas(numero.value,nome.value,data.value),screenModel,clientes)}){
                            Icon(imageVector =  Icons.Default.Print,"icone de imprimir")
                        }
                        IconButton(onClick = {dialogState.value = salvar(Entregas(numero.value,nome.value,data.value),screenModel,clientes)}){
                            Icon(imageVector =  Icons.Default.Save,"icone de salvar")
                        }
                    }
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceAround,Alignment.CenterVertically){
                        OutlinedTextField(numero.value.toString(),{numero.value = it.toLongOrNull()?: 0},label = {Text("N° da entrega")},keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(nome.value,{nome.value = it},label = {Text("Nome da entrega")})
                        OutlinedTextField(data.value,{}, modifier = Modifier.onFocusEvent {
                            if (it.isFocused) {
                                showDatePickerDialog = true
                                focusManager.clearFocus(force = true)
                            }
                        },label = {Text("Data de Saída")},readOnly = true)
                    }
                    Row(Modifier.fillMaxWidth().padding(20.dp),Arrangement.Center,Alignment.CenterVertically){
                        OutlinedTextField(clientecodigo.value.toString(),{clientecodigo.value = it.toLongOrNull()?: 0},modifier = Modifier.padding(5.dp),label = {Text("Cliente")},keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        IconButton(modifier = Modifier.padding(5.dp).background(Color.LightGray),onClick = {clientes.add(adicionarCliente(clientecodigo.value,screenModel));clientecodigo.value = 0}){
                            Icon(imageVector =  Icons.Default.PersonAdd,"icone de adicionar")
                        }
                    }
                    Row{
                        Text("Quatidade de entregas: "+clientes.size.toString(), style = TextStyle(fontSize = 20.sp))
                    }
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceAround,Alignment.CenterVertically){
                        Box(Modifier.fillMaxSize().padding(50.dp).align(Alignment.CenterVertically)){
                            val stateScroll = rememberLazyListState()
                            LazyColumn(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,state = stateScroll) {
                                item {
                                    Row(Modifier.fillMaxWidth().border(1.dp, Color.Black)){
                                        Text("Entrega", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text("Código", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text("Nome Fantasia", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text("Cidade", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text("Bairro", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text("Excluir", Modifier.padding(8.dp))
                                    }
                                }
                                var cor: Color
                                itemsIndexed(clientes){i,cliente ->
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
                                        Text(cliente.bairro.uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        IconButton(onClick = {clienteindex.value = i;deleteState.value = true}){
                                            Column(horizontalAlignment = Alignment.CenterHorizontally){
                                                Icon(imageVector =  Icons.Default.Delete,"icone de excluir")
                                            }
                                        }
                                    }
                                }
                            }
                            VerticalScrollbar(
                                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                adapter = rememberScrollbarAdapter(
                                    scrollState = stateScroll
                                )
                            )
                        }
                    }
                }
            }
            if (dialogState.value) {
                AlertDialog(onDismissRequest = {dialogState.value = false;navigator.popUntilRoot()},
                    title = { Text("AVISO") },
                    text = { Text("Tabela Salva com Sucesso") },
                    confirmButton = {
                        Button(onClick = {dialogState.value = false;navigator.popUntilRoot()}) {
                            Text("OK")
                        }
                    }
                )
            }
            if (deleteState.value) {
                AlertDialog(onDismissRequest = {deleteState.value = false},
                    title = { Text("AVISO") },
                    text = { Text("Tem certesa de excluir o cliente") },
                    dismissButton = {
                        Button(onClick = {deleteState.value = false}) {
                            Text("Não")
                        }
                    },
                    confirmButton = {
                        Button(onClick = {deleteState.value = false;clientes.removeAt(clienteindex.value)}) {
                            Text("Sim")
                        }
                    }
                )
            }
        }
    }
    private fun adicionarCliente(id:Long, screenModel:NewTabelaScreenModel): Cliente {
        return screenModel.addCliente(id)
    }
    private fun salvar(entrega: Entregas, screenModel: NewTabelaScreenModel, clientes:List<Cliente>):Boolean{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatteroriginal = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDateTime = LocalDate.parse(entrega.data,formatteroriginal)
        val dataf = localDateTime.format(formatter).toString()
        entrega.data = dataf
        entrega.clientes = clientes
        screenModel.criarEntrega(entrega)
        return true
    }
    private fun imprimirSave(entrega: Entregas, screenModel: NewTabelaScreenModel, clientes:List<Cliente>):Boolean{
        if(salvar(entrega,screenModel,clientes)){
            imprimir(entrega)
        }
        return true
    }
}