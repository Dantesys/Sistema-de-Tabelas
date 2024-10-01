package data.dao

import com.dantesys.Database
import com.dantesys.EntregaClienteQueries
import com.dantesys.EntregaQueries
import data.Cliente
import data.Entregas

class EntregasDAO{
    companion object {
        fun selecionaInicio(db: Database):List<Entregas>{
            val entregaQueries: EntregaQueries = db.entregaQueries
            val entregas = entregaQueries.selectJoin().executeAsList()
            return entregas.map { e ->
                Entregas(e.id,e.nome,e.data_)
            }
        }
        fun contarClientesInicio(db:Database):List<Long>{
            val entregaQueries: EntregaQueries = db.entregaQueries
            val entregaClienteQueries: EntregaClienteQueries = db.entregaClienteQueries
            val entregas = entregaQueries.selectJoin().executeAsList()
            val contados:MutableList<Long> = mutableListOf()
            entregas.map { e ->
                contados.add(entregaClienteQueries.contarEntregaCliente(e.id).executeAsOne())
            }
            return contados
        }
        fun selecionaEntrega(db:Database,id:Long):Entregas{
            val entregaQueries: EntregaQueries = db.entregaQueries
            val entregaClienteQueries: EntregaClienteQueries = db.entregaClienteQueries
            val entrega = entregaQueries.selectEntregaID(id).executeAsOne()
            val cliente = entregaClienteQueries.selecionarClientesbyEntrega(id).executeAsList()
            val clientes:MutableList<Cliente> = mutableListOf()
            cliente.map { c ->
                var cli = Cliente(c.clienteID)
                if(c.nome != null){
                    cli.nome = c.nome
                }
                if(c.cidade != null){
                    cli.cidade = c.cidade
                }
                if(c.bairro != null){
                    cli.bairro = c.bairro
                }
                clientes.add(cli)
            }
            val retorno = Entregas(entrega.id,entrega.nome,entrega.data_)
            retorno.clientes = clientes
            return retorno
        }
    }
}