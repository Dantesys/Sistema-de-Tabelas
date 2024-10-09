package models

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.Entregas
import data.dao.EntregasDAO
import kotlinx.coroutines.launch

class ListTabelaScreenModel : StateScreenModel<ListTabelaScreenModel.State>(State.Loading)  {
    sealed class State {
        data object Loading : State()
        data class Result(val entrega: List<Entregas>) : State()
    }
    fun getEntregas(limit:Long,pagina:Long,fid:Long=-1,fnome:String="",fdata:String="",fpendencia:Int=-1){
        screenModelScope.launch {
            mutableState.value = State.Loading
            mutableState.value = State.Result(EntregasDAO.selecionaEntregaPG(limit,pagina,fid,fnome,fdata,fpendencia))
        }
    }
}