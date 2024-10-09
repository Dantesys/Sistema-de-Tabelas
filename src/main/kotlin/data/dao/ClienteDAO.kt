package data.dao

import com.dantesys.Database
import data.Cliente

class ClienteDAO {
    companion object {
        fun addFast(db:Database,codigo:Long):Cliente{
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
        fun edit(db: Database,cliente: Cliente){
            val clienteQueries = db.clienteQueries
            clienteQueries.insertComplet(cliente.codigo,cliente.nome,cliente.cidade,cliente.bairro)
        }
    }
}