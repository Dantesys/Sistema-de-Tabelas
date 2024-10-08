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
                Entregas(e.id,e.nome,e.data_, e.pedencia == 1L)
            }
        }
        fun removeAll(db:Database,entregas: Entregas){
            val entregaClienteQueries = db.entregaClienteQueries
            entregaClienteQueries.removerEntrega(entregas.id)
        }
        fun adicionar(db:Database, entregas: Entregas){
            val entregaQueries = db.entregaQueries
            var pedencia = entregas.clientes.size
            var pos:Long = 1
            entregaQueries.adicionar(entregas.id,entregas.nome,entregas.data,1)
            entregas.clientes.map { c ->
                adicionarCliente(db,c.codigo,entregas.id,pos)
                pos++
                if(c.nome!="" && c.cidade!="" && c.bairro!=""){
                    pedencia--
                }
            }
            if(pedencia<=0){
                entregaQueries.adicionar(entregas.id,entregas.nome,entregas.data,0)
            }else{
                entregaQueries.adicionar(entregas.id,entregas.nome,entregas.data,1)
            }
        }
        fun adicionarCliente(db:Database,clienteID:Long,entregaID:Long,posicao:Long){
            val entregaClienteQueries = db.entregaClienteQueries
            entregaClienteQueries.adicionar(clienteID,posicao,entregaID)
        }
        fun getPedencias(db:Database):List<Entregas>{
            val entregaQueries: EntregaQueries = db.entregaQueries
            val entregas = entregaQueries.selectJoinP().executeAsList()
            return entregas.map { e ->
                Entregas(e.id,e.nome,e.data_,e.pedencia == 1L)
            }
        }
        fun contarClientesPendenciaInicio(db:Database):List<Long>{
            val entregaQueries: EntregaQueries = db.entregaQueries
            val entregaClienteQueries: EntregaClienteQueries = db.entregaClienteQueries
            val entregas = entregaQueries.selectJoinP().executeAsList()
            val contados:MutableList<Long> = mutableListOf()
            entregas.map { e ->
                contados.add(entregaClienteQueries.contarEntregaCliente(e.id).executeAsOne())
            }
            return contados
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
                val cli = Cliente(c.clienteID)
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
            val retorno = Entregas(entrega.id,entrega.nome,entrega.data_,entrega.pedencia == 1L)
            retorno.clientes = clientes
            return retorno
        }
    }
}