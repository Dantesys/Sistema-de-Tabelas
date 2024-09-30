package data

data class Entregas(val id:Long, val nome:String, val data:String){
    var clientes:List<Cliente> = arrayListOf();
}
