package telas.parts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.dantesys.sistemadetabelas.generated.resources.Res
import com.dantesys.sistemadetabelas.generated.resources.logo
import org.jetbrains.compose.resources.imageResource
import repository.Repository
import telas.ListClienteScreen
import telas.ListTabelaScreen
import telas.NewTabelaScreen

@Composable
fun menu(modifier: Modifier,arrangement:Arrangement.Vertical,alignment:Alignment.Horizontal,navigator: Navigator,inicio:Boolean=true,ntabela:Boolean=false,vtabela:Boolean=false,vcliente:Boolean=false){
    val codigo = remember {mutableLongStateOf(0L)}
    val nome = remember {mutableStateOf("")}
    val cidade = remember {mutableStateOf("")}
    val bairro = remember {mutableStateOf("")}
    val newState = remember { mutableStateOf(false) }
    Column(
        modifier,
        arrangement,
        alignment
    ) {
        if(newState.value){
            AlertDialog(onDismissRequest = {newState.value = false},
                title = {Text("Adicionar")},
                text = {
                    Column{
                        OutlinedTextField(codigo.value.toString(),{codigo.value = it.toLongOrNull()?: 0},label = {Text("Código")},keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(nome.value,{nome.value = it},label = {Text("Nome Fantasia")})
                        OutlinedTextField(cidade.value,{cidade.value = it},label = {Text("Cidade")})
                        OutlinedTextField(bairro.value,{bairro.value = it},label = {Text("Bairro")})
                    }
                },
                backgroundColor = Color.LightGray,
                dismissButton = {
                    Button(onClick = {newState.value = false}) {
                        Text("Cancelar")
                    }
                },
                confirmButton = {
                    Button(onClick = {Repository.addCliente(codigo.value,nome.value,cidade.value,bairro.value);newState.value = false}) {
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
            Button(onClick = {navigator.popUntilRoot()}, Modifier.fillMaxWidth(0.9f)){
                Text("Inicio")
            }
        }
        Button(onClick = {newState.value = true}, Modifier.fillMaxWidth(0.9f)){
            Text("Novo Clientes")
        }
        if(!vcliente){
            Button(onClick = {navigator.push(ListClienteScreen())}, Modifier.fillMaxWidth(0.9f)){
                Text("Ver Clientes")
            }
        }
        if(!ntabela){
            Button(onClick = {navigator.push(NewTabelaScreen())}, Modifier.fillMaxWidth(0.9f)){
                Text("Nova Tabela")
            }
        }
        if(!vtabela){
            Button(onClick = {navigator.push(ListTabelaScreen())}, Modifier.fillMaxWidth(0.9f)){
                Text("Ver Tabelas")
            }
        }
    }
}