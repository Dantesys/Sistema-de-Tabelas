package repository.dao

import com.dantesys.Database
import com.dantesys.Entrega
import com.dantesys.EntregaQueries
import repository.getDB

class EntregaDAO{
    companion object {
        private val db:Database = getDB()
        fun selecionaInicio():List<Entrega>{
            val entregaQueries: EntregaQueries = db.entregaQueries
            return entregaQueries.selectJoin().executeAsList()
        }
        fun getPedencias():List<Entrega>{
            val entregaQueries: EntregaQueries = db.entregaQueries
            return entregaQueries.selectJoinP().executeAsList()
        }
        fun novaEntrega(entrega: Entrega){
            val entregaQueries: EntregaQueries = db.entregaQueries
            entregaQueries.adicionar(entrega.id,entrega.nome,entrega.data_,entrega.pedencia)
        }
        fun selecionaEntrega(id:Long): Entrega {
            val entregaQueries: EntregaQueries = db.entregaQueries
            return entregaQueries.selectEntregaID(id).executeAsOne()
        }
        fun contar(filtro:String):Long{
            val entregaQueries: EntregaQueries = db.entregaQueries
            return entregaQueries.contarFiltro(filtro).executeAsOne()
        }
        fun getEntregaByFiltro(filtro:String,limit:Long,offset:Long):List<Entrega>{
            val entregaQueries: EntregaQueries = db.entregaQueries
            return entregaQueries.selectPGEntregaFiltro(filtro,limit,offset).executeAsList()
        }
    }
}