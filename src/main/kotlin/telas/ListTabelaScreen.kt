package telas

import androidx.compose.runtime.*
import androidx.paging.PagingData
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
        when (val result = state) {
            is ListTabelaScreenModel.State.Loading -> loadingContent(navigator)
            is ListTabelaScreenModel.State.Result -> list(result.entregas)
        }
        LaunchedEffect(currentCompositeKeyHash){
            screenModel.getEntregas()
        }

    }
    fun list(pg:MutableList<PagingData<Entrega>>){
        println(pg.size)
    }
}