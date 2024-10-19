package telas

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dantesys.Cliente
import com.dantesys.Entrega
import models.NewTabelaScreenModel
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import telas.parts.menu
import util.imprimir
import util.toBrazilianDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NewTabelaScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { NewTabelaScreenModel() }
        val nome = remember {mutableStateOf("")}
        val data = remember {mutableStateOf("")}
        val numero = remember {mutableLongStateOf(0)}
        val dialogState = remember {mutableStateOf(false)}
        val deleteState = remember {mutableStateOf(false)}
        val clientecodigo = remember {mutableLongStateOf(0)}
        val clientes = remember { mutableStateListOf(Cliente(0,"","","")) }
        clientes.remove(Cliente(0,"","",""))
        val stateList = rememberReorderableLazyListState(onMove = { from, to ->
            println("ANTES :${clientes.toList()}")
            val aux = clientes[from.index]
            clientes.removeAt(from.index)
            clientes.add(to.index, aux)
            println("DEPOIS:${clientes.toList()}")
        })
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
                                datePickerState.selectedDateMillis?.let { millis ->
                                    data.value = millis.toBrazilianDateFormat()
                                }
                                showDatePickerDialog = false
                            },border = BorderStroke(2.dp,Color.Green),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(204,255,204),contentColor = Color.Green)
                        ){
                            Text(text = "Selecionar")
                        }
                    }
                ) {
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally){
                            Button(onClick = {dialogState.value = imprimirSave(Entrega(numero.value,nome.value,data.value,0L),screenModel,clientes)},
                                border = BorderStroke(2.dp,Color.Black),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(232,232,232),contentColor = Color.Black)){
                                Text("Salvar e Imprimir")
                                Icon(imageVector =  Icons.Default.Print,"icone de imprimir")
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally){
                            Button(onClick = {dialogState.value = salvar(Entrega(numero.value,nome.value,data.value,0L),screenModel,clientes)},
                                border = BorderStroke(2.dp,Color.Black),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(232,232,232),contentColor = Color.Black)){
                                Text("Salvar")
                                Icon(imageVector =  Icons.Default.Save,"icone de salvar")
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceAround,Alignment.CenterVertically){
                        OutlinedTextField(numero.value.toString(),{numero.value = it.toLongOrNull()?: 0},label = {Text("N° da entrega")},keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(232,253,44), focusedLabelColor = Color(232,253,44)))
                        OutlinedTextField(nome.value,{nome.value = it},label = {Text("Nome da entrega")},colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(232,253,44), focusedLabelColor = Color(232,253,44)))
                        OutlinedTextField(data.value,{}, modifier = Modifier.onFocusEvent {
                            if (it.isFocused) {
                                showDatePickerDialog = true
                                focusManager.clearFocus(force = true)
                            }
                        },label = {Text("Data de Saída")},readOnly = true, trailingIcon = {
                            Icon(imageVector =  Icons.Default.EditCalendar,"icone de calendario")
                        },colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(232,253,44), focusedLabelColor = Color(232,253,44)))
                    }
                    Row(Modifier.fillMaxWidth().padding(20.dp),Arrangement.Center,Alignment.CenterVertically){
                        OutlinedTextField(clientecodigo.value.toString(),{clientecodigo.value = it.toLongOrNull()?: clientecodigo.value},
                            modifier = Modifier.padding(5.dp).onKeyEvent{
                                if(it.type == KeyEventType.KeyUp && (it.key == Key.Enter) || (it.key==Key.NumPadEnter)){
                                    clientes.add(adicionarCliente(clientecodigo.value,screenModel))
                                    clientecodigo.value = 0
                                }
                                false
                            },label = {Text("Cliente")},
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,imeAction = ImeAction.Done),
                            trailingIcon = {Icon(imageVector =  Icons.Default.PersonAdd,"icone de adicionar") },
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    clientes.add(adicionarCliente(clientecodigo.value,screenModel))
                                    clientecodigo.value = 0
                                }
                            ),colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(232,253,44), focusedLabelColor = Color(232,253,44))
                        )
                    }
                    Row{
                        Text("Quatidade de entregas: ${clientes.size}", style = TextStyle(fontSize = 20.sp))
                    }
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceAround,Alignment.CenterVertically){
                        Box(Modifier.fillMaxSize().padding(50.dp).align(Alignment.CenterVertically)){
                            LazyColumn(Modifier.fillMaxSize().reorderable(stateList).detectReorderAfterLongPress(stateList), horizontalAlignment = Alignment.CenterHorizontally,state = stateList.listState) {
                                stickyHeader {
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
                                    ReorderableItem(stateList,key=cliente){ isDragging ->
                                        val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                                        if(isDragging){
                                            cor=Color.Green
                                        }
                                        Row(Modifier.fillMaxWidth().background(cor).shadow(elevation.value)){
                                            Text("$num°", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                            Text(cliente.codigo.toString(), Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                            if(cliente.nome!=null){
                                                Text(cliente.nome.uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                            }else{
                                                Text("", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                            }
                                            if(cliente.cidade!=null){
                                                Text(cliente.cidade.uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                            }else{
                                                Text("", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                            }
                                            if(cliente.bairro!=null){
                                                Text(cliente.bairro.uppercase(), Modifier.padding(8.dp))
                                            }else{
                                                Text("", Modifier.padding(8.dp))
                                            }
                                            IconButton(onClick = {clienteindex.value = i;deleteState.value = true}){
                                                Icon(imageVector =  Icons.Default.Delete,"icone de excluir")
                                            }
                                        }
                                    }
                                }
                            }
                            VerticalScrollbar(
                                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                adapter = rememberScrollbarAdapter(
                                    scrollState = stateList.listState
                                )
                            )
                        }
                    }
                }
            }
            if (dialogState.value) {
                AlertDialog(onDismissRequest = {dialogState.value = false;navigator.popUntilRoot()},
                    title = { Text("AVISO", style = TextStyle(fontSize = 30.sp)) },
                    text = { Text("Tabela Salva com Sucesso!", style = TextStyle(fontSize = 20.sp)) },
                    confirmButton = {
                        Button(onClick = {dialogState.value = false;navigator.popUntilRoot()},
                            border = BorderStroke(2.dp,Color.Green),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(204,255,204),contentColor = Color.Green)
                        ){
                            Text("OK")
                        }
                    }
                )
            }
            if (deleteState.value) {
                AlertDialog(onDismissRequest = {deleteState.value = false},
                    title = { Text("AVISO", style = TextStyle(fontSize = 30.sp)) },
                    text = { Text("Tem certesa de excluir o cliente!", style = TextStyle(fontSize = 20.sp)) },
                    dismissButton = {
                        Button(onClick = {deleteState.value = false},
                            border = BorderStroke(2.dp,Color.Red),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(255,204,204),contentColor = Color.Red)
                        ){
                            Text("Não")
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            deleteState.value = false
                            clientes.removeAt(clienteindex.value)
                        },
                            border = BorderStroke(2.dp,Color.Green),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(204,255,204),contentColor = Color.Green)
                        ){
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
    private fun salvar(entrega: Entrega, screenModel: NewTabelaScreenModel, clientes:List<Cliente>):Boolean{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatteroriginal = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDateTime = LocalDate.parse(entrega.data_,formatteroriginal)
        val dataf = localDateTime.format(formatter).toString()
        screenModel.criarEntrega(Entrega(entrega.id,entrega.nome,dataf,entrega.pedencia),clientes)
        return true
    }
    private fun imprimirSave(entrega: Entrega, screenModel: NewTabelaScreenModel, clientes:List<Cliente>):Boolean{
        if(salvar(entrega,screenModel,clientes)){
            imprimir(entrega,clientes)
        }
        return true
    }
}