package repository.dao

import com.dantesys.*
import repository.getDB

class PedidoDAO {
    companion object{
        private val db: Database = getDB()
        fun getEntregaByPedido(id:Long):List<Pedido>{
            val pedidoQueries = db.pedidoQueries
            return pedidoQueries.selecionarEntregasbyCliente(id).executeAsList()
        }
        fun remove(clienteID:Long,entregaID:Long){
            val pedidoQueries = db.pedidoQueries
            pedidoQueries.removerCliente(clienteID,entregaID)
        }
        fun removePos(clienteID:Long,pos:Long,entregaID:Long){
            val pedidoQueries = db.pedidoQueries
            pedidoQueries.removerCompleto(clienteID,pos,entregaID)
        }
        fun getPedidosByEntrega(id:Long): List<Pedido> {
            val pedidoQueries = db.pedidoQueries
            return pedidoQueries.selecionarClientesbyEntrega(id).executeAsList()
        }
        fun removeAll(entrega:Entrega){
            val pedidoQueries = db.pedidoQueries
            pedidoQueries.removerEntrega(entrega.id)
        }
        fun add(cliente:Long,entrega:Long,posicao:Long){
            val pedidoQueries = db.pedidoQueries
            pedidoQueries.adicionar(cliente,posicao,entrega)
        }
        fun contarClientes(id:Long):Long{
            val pedidoQueries = db.pedidoQueries
            return pedidoQueries.contarEntregaCliente(id).executeAsOne()
        }
    }
}