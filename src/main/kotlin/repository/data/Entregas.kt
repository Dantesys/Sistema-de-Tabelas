package repository.data

data class Entregas(var id:Long, var nome:String, var data:String, var pedencia:Boolean = true){
    var clientes:List<Cliente> = arrayListOf()
}
