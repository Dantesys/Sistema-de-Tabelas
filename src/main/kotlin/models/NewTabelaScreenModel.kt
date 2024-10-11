package models

import cafe.adriel.voyager.core.model.ScreenModel
import repository.data.Cliente
import repository.data.Entregas
import repository.dao.ClienteDAO
import repository.dao.EntregasDAO

class NewTabelaScreenModel : ScreenModel {
    fun addCliente(codigo: Long): Cliente {
        val cliente = ClienteDAO.addFast(codigo)
        return cliente
    }
    fun criarEntrega(entrega: Entregas){
        EntregasDAO.adicionar(entrega)
    }
}