package data.dao

import com.dantesys.Database
import com.dantesys.EntregaClienteQueries
import com.dantesys.EntregaQueries
import data.Cliente
import data.Entregas
import util.getDB

class EntregasDAO{
    companion object {
        val db:Database = getDB()
        fun selecionaInicio():List<Entregas>{
            val entregaQueries: EntregaQueries = db.entregaQueries
            val entregas = entregaQueries.selectJoin().executeAsList()
            return entregas.map { e ->
                Entregas(e.id,e.nome,e.data_, e.pedencia == 1L)
            }
        }
        fun entregasByCliente(codigo:Long):List<Entregas>{
            val entregaClienteQueries = db.entregaClienteQueries
            val dbEntrega = entregaClienteQueries.selecionarEntregasbyCliente(codigo).executeAsList()
            return dbEntrega.map { e ->
                selecionaEntrega(e.entregaID)
            }
        }
        fun removeAll(entregas: Entregas){
            val entregaClienteQueries = db.entregaClienteQueries
            entregaClienteQueries.removerEntrega(entregas.id)
        }
        fun atualizarPedencia(entrega:Entregas){
            var pedencia = entrega.clientes.size
            entrega.clientes.map { c ->
                if(c.nome != "" && c.cidade != "" && c.bairro != ""){
                   pedencia--
                }
            }
            val entregaQueries = db.entregaQueries
            entregaQueries.adicionar(entrega.id,entrega.nome,entrega.data,pedencia.toLong())
        }
        fun selecionaEntregaPG(limit:Long,pagina:Long,fid:Long,fnome:String,fdata:String,fpendencia:Int):List<Entregas>{
            val offset = limit*pagina
            val entregaQueries = db.entregaQueries
            val entregas = mutableListOf<Entregas>()
            val entregasPG = entregaQueries.selectPGEntrega(limit,offset).executeAsList()
            entregasPG.map { e ->
                entregas.add(Entregas(e.id,e.nome,e.data_,e.pedencia>0))
            }
            return entregas
        }
        fun adicionar(entregas: Entregas){
            val entregaQueries = db.entregaQueries
            var pedencia = entregas.clientes.size
            var pos:Long = 1
            entregaQueries.adicionar(entregas.id,entregas.nome,entregas.data,1)
            entregas.clientes.map { c ->
                adicionarCliente(c.codigo,entregas.id,pos)
                pos++
                if(c.nome!="" && c.cidade!="" && c.bairro!=""){
                    pedencia--
                }
            }
            entregaQueries.adicionar(entregas.id,entregas.nome,entregas.data,pedencia.toLong())
        }
        private fun adicionarCliente(clienteID:Long,entregaID:Long,posicao:Long){
            val entregaClienteQueries = db.entregaClienteQueries
            entregaClienteQueries.adicionar(clienteID,posicao,entregaID)
        }
        fun getPedencias():List<Entregas>{
            val entregaQueries: EntregaQueries = db.entregaQueries
            val entregas = entregaQueries.selectJoinP().executeAsList()
            return entregas.map { e ->
                Entregas(e.id,e.nome,e.data_,e.pedencia > 0L)
            }
        }
        fun contarClientesPendenciaInicio():List<Long>{
            val entregaQueries: EntregaQueries = db.entregaQueries
            val entregaClienteQueries: EntregaClienteQueries = db.entregaClienteQueries
            val entregas = entregaQueries.selectJoinP().executeAsList()
            val contados:MutableList<Long> = mutableListOf()
            entregas.map { e ->
                contados.add(entregaClienteQueries.contarEntregaCliente(e.id).executeAsOne())
            }
            return contados
        }
        fun contarClientesInicio():List<Long>{
            val entregaQueries: EntregaQueries = db.entregaQueries
            val entregaClienteQueries: EntregaClienteQueries = db.entregaClienteQueries
            val entregas = entregaQueries.selectJoin().executeAsList()
            val contados:MutableList<Long> = mutableListOf()
            entregas.map { e ->
                contados.add(entregaClienteQueries.contarEntregaCliente(e.id).executeAsOne())
            }
            return contados
        }
        fun selecionaEntrega(id:Long):Entregas{
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
            val retorno = Entregas(entrega.id,entrega.nome,entrega.data_,entrega.pedencia > 0L)
            retorno.clientes = clientes
            return retorno
        }
    }
}