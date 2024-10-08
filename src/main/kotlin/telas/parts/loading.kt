package telas.parts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.cash.sqldelight.db.SqlDriver
import cafe.adriel.voyager.navigator.Navigator
import com.dantesys.sistemadetabelas.generated.resources.Res
import com.dantesys.sistemadetabelas.generated.resources.logo
import org.jetbrains.compose.resources.imageResource
import telas.NTabelaScreen

@Composable
fun loadingContent(navigator: Navigator, inicio:Boolean, driver: SqlDriver) {
    MaterialTheme {
        Row(Modifier.fillMaxSize()){
            Column(
                Modifier.fillMaxWidth(0.2f).fillMaxHeight().background(Color(250,255,196)),
                Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    bitmap = imageResource(Res.drawable.logo),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(200.dp)
                )
                if(inicio){
                    Button(onClick = {navigator.popUntilRoot()}, Modifier.fillMaxWidth(0.9f)){
                        Text("Inicio")
                    }
                }
                Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                    Text("Ver Clientes")
                }
                Button(onClick = {navigator.push(NTabelaScreen(driver))}, Modifier.fillMaxWidth(0.9f)){
                    Text("Nova Tabela")
                }
                Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                    Text("Ver Tabelas")
                }
            }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}