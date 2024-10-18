import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import com.dantesys.Database
import com.dantesys.sistemadetabelas.generated.resources.Res
import com.dantesys.sistemadetabelas.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import telas.InicioScreen
import repository.getDriver
import java.io.File

fun main() = application {
    val file = File("sistema.db")
    if(!file.exists())
    {
        file.createNewFile()
    }
    Database.Schema.create(getDriver())
    Window(onCloseRequest = ::exitApplication,title = "Sistema de Tabelas", icon = painterResource(Res.drawable.logo)) {
        Navigator(InicioScreen())
    }
}