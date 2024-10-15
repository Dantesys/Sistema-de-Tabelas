package models

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dantesys.Entrega
import kotlinx.coroutines.*
import repository.Repository

class InicioScreenModel : StateScreenModel<InicioScreenModel.State>(State.Loading) {
    sealed class State {
        data object Loading : State()
        data class Result(val entregas: List<Entrega>, val clientes: List<Long>, val pedencias:List<Entrega>, val clientesp: List<Long>) : State()
    }
    fun getInicio(){
        screenModelScope.launch {
            mutableState.value = State.Loading
            mutableState.value = State.Result(Repository.getInicio().entregas,Repository.getInicio().clientes,Repository.getInicio().pedencias,Repository.getInicio().clientesp)
        }
    }
}