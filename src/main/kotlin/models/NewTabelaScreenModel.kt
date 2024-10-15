package models

import cafe.adriel.voyager.core.model.ScreenModel
import com.dantesys.Cliente
import com.dantesys.Entrega
import repository.Repository

class NewTabelaScreenModel : ScreenModel {
    fun addCliente(codigo: Long): Cliente {
        return Repository.addFastCliente(codigo)
    }
    fun criarEntrega(entrega: Entrega,clientes:List<Cliente>){
        Repository.addEntregaCliente(entrega,clientes)
    }
}