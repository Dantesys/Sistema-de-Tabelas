package telas.parts

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.dantesys.sistemadetabelas.generated.resources.Res
import com.dantesys.sistemadetabelas.generated.resources.logo
import org.jetbrains.compose.resources.imageResource
import telas.ListTabelaScreen
import telas.NewTabelaScreen

@Composable
fun menu(modifier: Modifier,arrangement:Arrangement.Vertical,alignment:Alignment.Horizontal,navigator: Navigator,inicio:Boolean=true,ntabela:Boolean=false,vtabela:Boolean=false,ncliente:Boolean=false,vcliente:Boolean=false){
    Column(
        modifier,
        arrangement,
        alignment
    ) {
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
        if(!ncliente){
            Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                Text("Novo Clientes")
            }
        }
        if(!vcliente){
            Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
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