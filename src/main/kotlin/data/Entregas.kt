package data

data class Entregas(val nome:String){
    val id:Int = 0;
    var data:String = "";
    var clientes:ArrayList<Cliente> = arrayListOf();
}
