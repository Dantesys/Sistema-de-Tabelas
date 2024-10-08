package models

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dantesys.Database
import data.Entregas
import data.dao.EntregasDAO
import kotlinx.coroutines.launch

class EditTabelaScreenModel : StateScreenModel<EditTabelaScreenModel.State>(State.Loading) {
    sealed class State {
        data object Loading : State()
        data class Result(val entrega: Entregas) : State()
    }
    fun getClientes(db:Database,id:Long){
        screenModelScope.launch {
            mutableState.value = State.Loading
            mutableState.value = State.Result(EntregasDAO.selecionaEntrega(db,id))
        }
    }
    fun criarEntrega(db:Database,entrega:Entregas){
        EntregasDAO.removeAll(db,entrega)
        EntregasDAO.adicionar(db,entrega)
    }
}