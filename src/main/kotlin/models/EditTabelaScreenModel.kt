package models

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import repository.data.Cliente
import repository.data.Entregas
import repository.dao.ClienteDAO
import repository.dao.EntregasDAO
import kotlinx.coroutines.launch

class EditTabelaScreenModel : StateScreenModel<EditTabelaScreenModel.State>(State.Loading) {
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
    fun editCliente(cliente: Cliente){
        ClienteDAO.edit(cliente)
        val entregas = EntregasDAO.entregasByCliente(cliente.codigo)
        entregas.map { e ->
            EntregasDAO.atualizarPedencia(e)
        }
    }
    fun criarEntrega(entrega: Entregas){
        EntregasDAO.removeAll(entrega)
        EntregasDAO.adicionar(entrega)
    }
}