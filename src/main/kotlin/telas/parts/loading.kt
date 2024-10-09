package telas.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator

@Composable
fun loadingContent(navigator: Navigator) {
    MaterialTheme {
        Row(Modifier.fillMaxSize()){
            menu(Modifier.fillMaxWidth(0.2f).fillMaxHeight().background(Color(250,255,196)),Arrangement.spacedBy(10.dp),Alignment.CenterHorizontally,navigator)
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