package models

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dantesys.Database
import data.Entregas
import data.dao.EntregasDAO
import kotlinx.coroutines.launch

class VTabelaScreenModel(val db: Database, val id:Long) : StateScreenModel<VTabelaScreenModel.State>(State.Loading) {
    sealed class State {
        object Loading : State()
        data class Result(val entrega: Entregas) : State()
    }
    fun getInicio(db:Database){
        screenModelScope.launch {
            mutableState.value = models.VTabelaScreenModel.State.Loading
            mutableState.value = models.VTabelaScreenModel.State.Result(EntregasDAO.selecionaEntrega(db,id))
        }
    }
}