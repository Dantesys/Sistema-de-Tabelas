package models

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dantesys.Cliente
import com.dantesys.Entrega
import kotlinx.coroutines.launch
import repository.Repository

class ViewTabelaScreenModel : StateScreenModel<ViewTabelaScreenModel.State>(State.Loading) {
    sealed class State {
        data object Loading : State()
        data class Result(val entrega: Entrega, val clientes:List<Cliente>) : State()
    }
    fun getClientes(id:Long){
        screenModelScope.launch {
            mutableState.value = State.Loading
            val resultado = Repository.getView(id)
            mutableState.value = State.Result(resultado.entrega,resultado.clientes)
        }
    }
}