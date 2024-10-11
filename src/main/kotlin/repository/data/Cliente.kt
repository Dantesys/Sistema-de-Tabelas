package repository.data

data class Cliente(
    val codigo:Long,
){
    var cidade:String = ""
    var bairro:String = ""
    var nome:String = ""
}