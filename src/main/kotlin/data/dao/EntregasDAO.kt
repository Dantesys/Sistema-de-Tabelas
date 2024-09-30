package data.dao

import com.dantesys.Database
import com.dantesys.Entrega
import com.dantesys.EntregaClienteQueries
import com.dantesys.EntregaQueries
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
        fun contarClientes(db:Database, id:Long):Long{
            val entregaClienteQueries: EntregaClienteQueries = db.entregaClienteQueries
            val pararetorno = entregaClienteQueries.contarEntregaCliente(id).executeAsOne()
            return pararetorno;
        }
    }
}