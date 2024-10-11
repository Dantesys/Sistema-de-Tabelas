package repository.dao

import com.dantesys.Database
import repository.data.Cliente
import repository.getDB

class ClienteDAO {
    companion object {
        val db:Database = getDB()
        fun addFast(codigo:Long): Cliente {
            val clienteQueries = db.clienteQueries
            val cliente = clienteQueries.selectClienteCodigo(codigo).executeAsOneOrNull()
            if(cliente == null){
                clienteQueries.insertFast(codigo)
                return Cliente(codigo)
            }
            val clienteNovo = Cliente(cliente.codigo)
            clienteNovo.nome = cliente.nome?: ""
            clienteNovo.cidade = cliente.cidade?: ""
            clienteNovo.bairro = cliente.bairro?: ""
            return clienteNovo
        }
        fun edit(cliente: Cliente){
            val clienteQueries = db.clienteQueries
            clienteQueries.insertComplet(cliente.codigo,cliente.nome,cliente.cidade,cliente.bairro)
        }
    }
}