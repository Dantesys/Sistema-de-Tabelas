package models

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dantesys.Cliente
import kotlinx.coroutines.launch
import models.ListTabelaScreenModel.State
import repository.Repository

class ListClienteScreenModel: StateScreenModel<ListClienteScreenModel.State>(State.Loading) {
    sealed class State {
        data object Loading : State()
        data class Result(val clientes:List<Cliente>, val pages:Long) : State()
    }
    fun getClientes(filtro:String="",pag:Int=1,codigo:Boolean=false,nome:Boolean=false,cidade:Boolean=false,bairro:Boolean=false){
        screenModelScope.launch {
            mutableState.value = State.Loading
            var cod = 0L
            var nom = 0L
            var cid = 0L
            var bai = 0L
            if(codigo){cod=1L}
            if(nome){nom=1L}
            if(cidade){cid=1L}
            if(bairro){bai=1L}
            val resultado = Repository.getListCliente(filtro,pag,cod,nom,cid,bai)
            mutableState.value = State.Result(resultado.clientes,resultado.pages)
        }
    }
    fun editCliente(cliente:Cliente,filtro:String="",pag:Int=1,codigo:Boolean=false,nome:Boolean=false,cidade:Boolean=false,bairro:Boolean=false){
        screenModelScope.launch {
            mutableState.value = State.Loading
            Repository.editCliente(cliente)
            var cod = 0L
            var nom = 0L
            var cid = 0L
            var bai = 0L
            if(codigo){cod=1L}
            if(nome){nom=1L}
            if(cidade){cid=1L}
            if(bairro){bai=1L}
            val resultado = Repository.getListCliente(filtro,pag,cod,nom,cid,bai)
            mutableState.value = State.Result(resultado.clientes,resultado.pages)
        }
    }
}