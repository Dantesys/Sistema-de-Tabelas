package models

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dantesys.Cliente
import com.dantesys.Entrega
import kotlinx.coroutines.launch
import repository.Repository

class EditTabelaScreenModel : StateScreenModel<EditTabelaScreenModel.State>(State.Loading) {
    sealed class State {
        data object Loading : State()
        data class Result(val entrega: Entrega,val clientes: List<Cliente>) : State()
    }
    fun getClientes(id:Long){
        screenModelScope.launch {
            mutableState.value = State.Loading
            val resultado = Repository.getView(id)
            mutableState.value = State.Result(resultado.entrega,resultado.clientes)
        }
    }
    fun editCliente(cliente: Cliente,id:Long,pos:Long,cant:Long){
        screenModelScope.launch {
            mutableState.value = State.Loading
            Repository.editCliente(cliente)
            Repository.attPedencia(cliente.codigo)
            Repository.attClienteEntrega(cliente.codigo,id,pos,cant)
            val resultado = Repository.getView(id)
            mutableState.value = State.Result(resultado.entrega,resultado.clientes)
        }
    }
    fun removerCliente(cliente:Long,pos:Long,entrega:Long){
        Repository.delClienteEntrega(cliente,pos,entrega)
    }
    fun editEntrega(entrega: Entrega,clientes:List<Cliente>){
        screenModelScope.launch {
            mutableState.value = State.Loading
            Repository.editEntrega(entrega, clientes)
            val resultado = Repository.getView(entrega.id)
            mutableState.value = State.Result(resultado.entrega,resultado.clientes)
        }
    }
}