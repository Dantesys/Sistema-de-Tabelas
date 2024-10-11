package models

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import repository.data.Entregas
import repository.dao.EntregasDAO
import kotlinx.coroutines.launch

class ViewTabelaScreenModel : StateScreenModel<ViewTabelaScreenModel.State>(State.Loading) {
    sealed class State {
        data object Loading : State()
        data class Result(val entrega: Entregas) : State()
    }
    fun getClientes(id:Long){
        screenModelScope.launch {
            mutableState.value = State.Loading
            mutableState.value = State.Result(EntregasDAO.selecionaEntrega(id))
        }
    }
}