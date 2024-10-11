package models

import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dantesys.Entrega
import repository.dao.EntregasDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class ListTabelaScreenModel : StateScreenModel<ListTabelaScreenModel.State>(State.Loading)  {
    sealed class State {
        data object Loading : State()
        data class Result(val entregas: MutableList<PagingData<Entrega>>) : State()
    }
    fun getEntregas(filtro:String="",fId:Long=0,fNome:Long=0,fData:Long=0,fDatai:String="",fDataf:String="",fPedencia:Long=0){
        screenModelScope.launch {
            mutableState.value = State.Loading
            val pgs = EntregasDAO.selecionaEntregaPG(filtro,fId,fNome,fData,fDatai,fDataf,fPedencia)
            val items:Flow<PagingData<Entrega>> = Pager(
                PagingConfig(pageSize = 15, enablePlaceholders = false)
            ){
                pgs
            }.flow.cachedIn(screenModelScope)
            val lista:MutableList<PagingData<Entrega>> = mutableListOf()
            items.toList(lista)
            mutableState.value = State.Result(lista)
        }
    }
}