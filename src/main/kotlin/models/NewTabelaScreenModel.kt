package models

import cafe.adriel.voyager.core.model.ScreenModel
import data.Cliente
import data.Entregas
import data.dao.ClienteDAO
import data.dao.EntregasDAO

class NewTabelaScreenModel : ScreenModel {
    fun addCliente(codigo: Long):Cliente{
        val cliente = ClienteDAO.addFast(codigo)
        return cliente
    }
    fun criarEntrega(entrega:Entregas){
        EntregasDAO.adicionar(entrega)
    }
}