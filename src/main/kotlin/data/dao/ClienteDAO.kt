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
            var cliente_novo = Cliente(cliente.codigo)
            cliente_novo.nome = cliente.nome?: ""
            cliente_novo.cidade = cliente.cidade?: ""
            cliente_novo.bairro = cliente.bairro?: ""
            return cliente_novo
        }
    }
}