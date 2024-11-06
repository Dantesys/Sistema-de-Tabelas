package telas.parts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import com.dantesys.sistemadetabelas.generated.resources.Res
import com.dantesys.sistemadetabelas.generated.resources.logo
import org.jetbrains.compose.resources.imageResource
import repository.Repository
import telas.ListClienteScreen
import telas.ListTabelaScreen
import telas.NewTabelaScreen
import util.importClientes

@Composable
fun menu(modifier: Modifier,arrangement:Arrangement.Vertical,alignment:Alignment.Horizontal,navigator: Navigator,inicio:Boolean=true,ntabela:Boolean=false,vtabela:Boolean=false,vcliente:Boolean=false){
    val codigo = remember {mutableLongStateOf(0L)}
    val nome = remember {mutableStateOf("")}
    val cidade = remember {mutableStateOf("")}
    val bairro = remember {mutableStateOf("")}
    val newState = remember { mutableStateOf(false) }
    val aviso = remember { mutableStateOf(0) }
    Column(
        modifier,
        arrangement,
        alignment
    ) {
        if(newState.value){
            AlertDialog(onDismissRequest = {newState.value = false},
                title = {Text("Adicionar", style = TextStyle(fontSize = 30.sp))},
                text = {
                    Column{
                        OutlinedTextField(codigo.value.toString(),{codigo.value = it.toLongOrNull()?: 0},label = {Text("Código")},keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(51,51,0), focusedLabelColor = Color(51,51,0)))
                        OutlinedTextField(nome.value,{nome.value = it},label = {Text("Nome Fantasia")},colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(51,51,0), focusedLabelColor = Color(51,51,0)))
                        OutlinedTextField(cidade.value,{cidade.value = it},label = {Text("Cidade")},colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(51,51,0), focusedLabelColor = Color(51,51,0)))
                        OutlinedTextField(bairro.value,{bairro.value = it},label = {Text("Bairro")},colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color(51,51,0), focusedLabelColor = Color(51,51,0)))
                    }
                },
                dismissButton = {
                    Button(onClick = {newState.value = false},
                        border = BorderStroke(2.dp,Color.Red),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(255,204,204),contentColor = Color.Red)) {
                        Text("Cancelar")
                    }
                },
                confirmButton = {
                    Button(onClick = {Repository.addCliente(codigo.value,nome.value,cidade.value,bairro.value);newState.value = false},
                        border = BorderStroke(2.dp,Color.Green),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(204,255,204),contentColor = Color.Green)) {
                        Text("Adicionar")
                    }
                }
            )
        }
        Image(
            bitmap = imageResource(Res.drawable.logo),
            contentDescription = "Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(200.dp)
        )
        if(!inicio){
            Button(onClick = {navigator.popUntilRoot()}, Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(10.dp)
            ){
                Icon(imageVector =  Icons.Default.Home,"icone de casa")
                Spacer(modifier = Modifier.padding(10.dp))
                Text("Inicio")
            }
        }
        Button(onClick = {newState.value = true}, Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(10.dp)){
            Icon(imageVector =  Icons.Default.PersonAdd,"icone de pessoa")
            Spacer(modifier = Modifier.padding(10.dp))
            Text("Novo Clientes")
        }
        if(!vcliente){
            Button(onClick = {navigator.push(ListClienteScreen())}, Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(10.dp)){
                Icon(imageVector =  Icons.Default.ViewAgenda,"icone de listar pessoa")
                Spacer(modifier = Modifier.padding(10.dp))
                Text("Ver Clientes")
            }
        }
        if(!ntabela){
            Button(onClick = {navigator.push(NewTabelaScreen())}, Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(10.dp)){
                Icon(imageVector =  Icons.Default.AddTask,"icone de nova tabela")
                Spacer(modifier = Modifier.padding(10.dp))
                Text("Nova Tabela")
            }
        }
        if(!vtabela){
            Button(onClick = {navigator.push(ListTabelaScreen())}, Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(10.dp)){
                Icon(imageVector =  Icons.Default.TableView,"icone de listar tabelas")
                Spacer(modifier = Modifier.padding(10.dp))
                Text("Ver Tabelas")
            }
        }
        Button(onClick = {aviso.value = importClientes()}, Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(10.dp)){
            Icon(imageVector =  Icons.Default.ImportExport,"icone de importação")
            Spacer(modifier = Modifier.padding(10.dp))
            Text("Importar Clientes")
        }
        if(aviso.value>0){
            AlertDialog(onDismissRequest = {aviso.value = 0},
                title = {Text("Aviso!", style = TextStyle(fontSize = 30.sp))},
                text = {
                    Column{
                        Text("${aviso.value} Clientes Importados Com Sucesso!", style = TextStyle(fontSize = 20.sp))
                    }
                },
                confirmButton = {
                    Button(onClick = {aviso.value = 0},
                        border = BorderStroke(2.dp,Color.Green),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(204,255,204),contentColor = Color.Green)) {
                        Text("Ok")
                    }
                }
            )
        }
    }
}