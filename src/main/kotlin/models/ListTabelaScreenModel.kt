package models

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dantesys.Entrega
import kotlinx.coroutines.launch
import repository.Repository

class ListTabelaScreenModel : StateScreenModel<ListTabelaScreenModel.State>(State.Loading)  {
    sealed class State {
        data object Loading : State()
        data class Result(val entregas:List<Entrega>, val clientes:List<Long>, val pages:Long) : State()
    }
    fun getEntregas(filtro:String="",pag:Int=1){
        screenModelScope.launch {
            mutableState.value = State.Loading
            val resultado = Repository.getListEntregas(filtro,pag)
            mutableState.value = State.Result(resultado.entrega,resultado.clientes,resultado.pages)
        }
    }
}