package repository.dao

import com.dantesys.Database
import com.dantesys.Cliente
import repository.getDB

class ClienteDAO {
    companion object {
        private val db:Database = getDB()
        fun addFast(codigo:Long): Cliente {
            val clienteQueries = db.clienteQueries
            val cliente = clienteQueries.selectClienteCodigo(codigo).executeAsOneOrNull()
            if(cliente == null){
                clienteQueries.insertFast(codigo)
                return Cliente(codigo,"","","")
            }
            return cliente
        }
        fun countFiltro(filtro:String,codigo:Long,nome:Long,cidade:Long,bairro:Long):Long{
            val clienteQueries = db.clienteQueries
            return clienteQueries.contarFiltro(filtro,codigo,nome,cidade,bairro).executeAsOne()
        }
        fun selectFiltro(filtro:String,codigo:Long,nome:Long,cidade:Long,bairro:Long,limit:Long,offset:Long):List<Cliente>{
            val clienteQueries = db.clienteQueries
            return clienteQueries.selectPGFiltro(filtro,codigo,nome,cidade,bairro,limit,offset).executeAsList()
        }
        fun add(codigo:Long,nome:String,cidade:String,bairro:String){
            val clienteQueries = db.clienteQueries
            clienteQueries.insertComplet(codigo,nome,cidade,bairro)
        }
        fun edit(cliente: Cliente){
            val clienteQueries = db.clienteQueries
            clienteQueries.insertComplet(cliente.codigo,cliente.nome,cliente.cidade,cliente.bairro)
        }
        fun getId(id:Long):Cliente{
            val clienteQueries = db.clienteQueries
            return clienteQueries.selectClienteCodigo(id).executeAsOne()
        }
    }
}