package data.dao

import app.cash.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import com.dantesys.Database
import com.dantesys.Entrega
import com.dantesys.EntregaClienteQueries
import com.dantesys.EntregaQueries
import data.Cliente
import data.Entregas
import kotlinx.coroutines.Dispatchers
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
        fun selecionaEntregaPG(filtro:String,fId:Int,fNome:Int,fData:Int,fDatai:String,fDataf:String,fPedencia:Int): PagingSource<Int, Entrega> {
            val entregaQueries = db.entregaQueries
            val pagingSource = QueryPagingSource(
                countQuery = entregaQueries.contarFiltro(filtro,fId,fNome,fData,fDatai,fDataf,fPedencia),
                transacter = entregaQueries,
                context = Dispatchers.IO,
                queryProvider = { limit,offset ->
                    entregaQueries.selectPGEntregaFiltro(filtro,fId,fNome,fData,fDatai,fDataf,fPedencia,limit,offset)
                }
            )
            return pagingSource
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