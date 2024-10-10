package models

import androidx.paging.PagingData
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dantesys.Entrega
import data.dao.EntregasDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ListTabelaScreenModel : StateScreenModel<ListTabelaScreenModel.State>(State.Loading)  {
    sealed class State {
        data object Loading : State()
        data class Result(val entregas: PagingSource<Int, Entrega>) : State()
    }
    fun getEntregas(filtro:String="",fId:Int=0,fNome:Int=0,fData:Int=0,fDatai:String="",fDataf:String="",fPedencia:Int=0){
        screenModelScope.launch {
            mutableState.value = State.Loading
            val pgs = EntregasDAO.selecionaEntregaPG(filtro,fId,fNome,fData,fDatai,fDataf,fPedencia)
            val items:Flow<PagingData<Entrega>> = Pager(
                config = PagingConfig(pageSize = 15, enablePlaceholders = false),
                pagingSourceFactory = { pgs }
            ).flow
            //TODO
            mutableState.value = State.Result(pgs)
        }
    }
}