package models

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dantesys.Database
import data.Entregas
import data.dao.EntregasDAO
import kotlinx.coroutines.launch

class NTabelaScreenModel() : StateScreenModel<NTabelaScreenModel.State>(State.Loading) {
    sealed class State {
        object Loading : State()
        data class Result(val entrega: Entregas) : State()
    }
    fun getClientes(db:Database,id:Long){
        screenModelScope.launch {
            mutableState.value = models.NTabelaScreenModel.State.Loading
            mutableState.value = models.NTabelaScreenModel.State.Result(EntregasDAO.selecionaEntrega(db,id))
        }
    }
}