package models

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dantesys.Database
import data.Entregas
import data.dao.EntregasDAO
import kotlinx.coroutines.*

class InicioScreenModel(val db:Database) : StateScreenModel<InicioScreenModel.State>(State.Loading) {
    sealed class State {
        object Loading : State()
        data class Result(val entregas: List<Entregas>, val clientes: List<Long>) : State()
    }
    fun getInicio(db:Database){
        screenModelScope.launch {
            mutableState.value = State.Loading
            mutableState.value = State.Result(EntregasDAO.selecionaInicio(db), EntregasDAO.contarClientesInicio(db))
        }
    }
}