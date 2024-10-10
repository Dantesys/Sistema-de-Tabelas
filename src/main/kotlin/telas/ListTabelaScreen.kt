package telas

import androidx.compose.runtime.*
import androidx.paging.PagingSource
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dantesys.Entrega
import models.ListTabelaScreenModel
import telas.parts.loadingContent

class ListTabelaScreen:Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { ListTabelaScreenModel() }
        val state by screenModel.state.collectAsState()
        val pgs:PagingSource<Int,Entrega>
        val load = remember { mutableStateOf(false) }
        when (val result = state) {
            is ListTabelaScreenModel.State.Loading -> loadingContent(navigator)
            is ListTabelaScreenModel.State.Result -> {pgs=result.entregas;load.value=true}
        }
        LaunchedEffect(currentCompositeKeyHash){
            screenModel.getEntregas()
        }
        if(load.value){
        }
    }
}