package repository

import com.dantesys.Cliente
import com.dantesys.Entrega
import repository.dao.ClienteDAO
import repository.dao.EntregaDAO
import repository.dao.PedidoDAO

class Repository {
    data class ResultInicio(val entregas: List<Entrega>, val clientes: List<Long>, val pedencias:List<Entrega>, val clientesp: List<Long>)
    data class ResultView(val entrega: Entrega, val clientes: List<Cliente>)
    data class ResultList(val entrega: List<Entrega>, val clientes: List<Long>,val pages:Long)
    data class ResultListC(val clientes: List<Cliente>, val pages:Long)
    companion object {
        fun getView(id:Long):ResultView{
            val pedidos = PedidoDAO.getPedidosByEntrega(id)
            return ResultView(EntregaDAO.selecionaEntrega(id),pedidos.map{ p ->
                val temp = ClienteDAO.getId(p.clienteID)
                var nome = ""
                if(temp.nome!=null){
                    nome = temp.nome
                }
                var cidade = ""
                if(temp.cidade!=null){
                    cidade = temp.cidade
                }
                var bairro = ""
                if(temp.bairro!=null){
                    bairro = temp.bairro
                }
                Cliente(temp.codigo,nome,cidade,bairro)
            })
        }
        fun addFastCliente(id:Long):Cliente{
            return ClienteDAO.addFast(id)
        }
        fun addCliente(codigo:Long,nome:String,cidade:String,bairro:String){
            ClienteDAO.add(codigo,nome,cidade,bairro)
        }
        fun editCliente(cliente:Cliente){
            ClienteDAO.edit(cliente)
        }
        fun attPedencia(id:Long){
            val pedidos = PedidoDAO.getEntregaByPedido(id)
            pedidos.map { p ->
                val pEntrega = EntregaDAO.selecionaEntrega(p.entregaID)
                val pedencia = pEntrega.pedencia-1
                EntregaDAO.novaEntrega(Entrega(pEntrega.id,pEntrega.nome,pEntrega.data_,pedencia))
            }
        }
        fun attClienteEntrega(cliente:Long,entrega:Long,pos:Long,cant:Long){
            PedidoDAO.remove(cant,entrega)
            PedidoDAO.add(cliente,entrega,pos)
        }
        fun delClienteEntrega(cliente:Long,pos:Long,entrega:Long){
            PedidoDAO.removePos(cliente,pos,entrega)
        }
        fun editEntrega(entrega: Entrega,clientes: List<Cliente>){
            PedidoDAO.removeAll(entrega)
            addEntregaCliente(entrega,clientes)
        }
        fun getListEntregas(filtro:String,pag:Int):ResultList{
            val limite = 10f
            val offset = 10f*(pag-1)
            val filtrolikes = "%$filtro%"
            val qtd = EntregaDAO.contar(filtrolikes)
            var pages = 1L
            if(qtd>limite){
                val div = qtd.div(limite).toLong()
                pages = (div+(div%2))
            }
            val entregas = EntregaDAO.getEntregaByFiltro(filtrolikes,limite.toLong(),offset.toLong())
            val qtdClientes = mutableListOf<Long>()
            entregas.map { e ->
                qtdClientes.add(PedidoDAO.contarClientes(e.id))
            }
            return ResultList(entregas,qtdClientes,pages)
        }
        fun getListCliente(filtro:String,pag:Int,codigo:Long,nome:Long,cidade:Long,bairro:Long):ResultListC{
            val limite = 10f
            val offset = 10f*(pag-1)
            val filtrolikes = "%$filtro%"
            val qtd = ClienteDAO.countFiltro(filtrolikes,codigo,nome,cidade,bairro)
            var pages = 1L
            if(qtd>limite){
                val div = qtd.div(limite).toLong()
                pages = (div+(div%2))
            }
            val clientes = ClienteDAO.selectFiltro(filtrolikes,codigo,nome,cidade,bairro,limite.toLong(),offset.toLong())
            return ResultListC(clientes,pages)
        }
        fun getInicio():ResultInicio{
            val entregasP = EntregaDAO.getPedencias()
            val qtdClientesP = mutableListOf<Long>()
            entregasP.map { e ->
                qtdClientesP.add(PedidoDAO.contarClientes(e.id))
            }
            var limit = 5L
            if(entregasP.isEmpty()){
                limit = 10L
            }
            val entregas = EntregaDAO.selecionaInicio(limit)
            val qtdClientes = mutableListOf<Long>()
            entregas.map { e ->
                qtdClientes.add(PedidoDAO.contarClientes(e.id))
            }
            return ResultInicio(entregas,qtdClientes,entregasP,qtdClientesP)
        }
        fun addEntregaCliente(entrega:Entrega, clientes:List<Cliente>){
            var pendencia = 0L
            clientes.map { c ->
                if(c.nome==null || c.nome=="" || c.bairro==null || c.bairro=="" || c.cidade==null || c.cidade==""){
                    pendencia++
                }
            }
            EntregaDAO.novaEntrega(Entrega(entrega.id,entrega.nome,entrega.data_,pendencia))
            var pos = 1L
            clientes.map{ c ->
                PedidoDAO.add(c.codigo,entrega.id,pos)
                pos++
            }
        }
    }
}