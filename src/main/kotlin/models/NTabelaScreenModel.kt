package models

import cafe.adriel.voyager.core.model.ScreenModel
import com.dantesys.Database
import data.Cliente
import data.Entregas
import data.dao.ClienteDAO
import data.dao.EntregasDAO

class NTabelaScreenModel : ScreenModel {
    fun addCliente(db:Database,codigo: Long):Cliente{
        val cliente = ClienteDAO.addFast(db,codigo)
        return cliente
    }
    fun criarEntrega(db:Database,entrega:Entregas){
        EntregasDAO.adicionar(db,entrega)
    }
}