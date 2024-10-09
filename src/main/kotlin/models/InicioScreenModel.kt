package models

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.Entregas
import data.dao.EntregasDAO
import kotlinx.coroutines.*

class InicioScreenModel : StateScreenModel<InicioScreenModel.State>(State.Loading) {
    sealed class State {
        data object Loading : State()
        data class Result(val entregas: List<Entregas>, val clientes: List<Long>,val pedencias:List<Entregas>,val clientesp: List<Long>) : State()
    }
    fun getInicio(){
        screenModelScope.launch {
            mutableState.value = State.Loading
            mutableState.value = State.Result(EntregasDAO.selecionaInicio(), EntregasDAO.contarClientesInicio(), EntregasDAO.getPedencias(),EntregasDAO.contarClientesPendenciaInicio())
        }
    }
}