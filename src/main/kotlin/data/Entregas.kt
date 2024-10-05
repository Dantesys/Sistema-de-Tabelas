package data

data class Entregas(var id:Long, var nome:String, var data:String){
    var clientes:List<Cliente> = arrayListOf();
}
