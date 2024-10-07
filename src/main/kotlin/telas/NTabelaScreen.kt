package telas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.cash.sqldelight.db.SqlDriver
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dantesys.Database
import com.dantesys.sistemadetabelas.generated.resources.Res
import com.dantesys.sistemadetabelas.generated.resources.logo
import data.Cliente
import data.Entregas
import models.NTabelaScreenModel
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.apache.pdfbox.printing.PDFPageable
import org.jetbrains.compose.resources.imageResource
import org.vandeseer.easytable.TableDrawer
import org.vandeseer.easytable.settings.HorizontalAlignment
import org.vandeseer.easytable.structure.Table
import org.vandeseer.easytable.structure.cell.TextCell
import java.awt.print.PrinterJob
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.Sides
import java.awt.Color as JColor
import org.vandeseer.easytable.structure.Row as TRow

class NTabelaScreen(val driver: SqlDriver) : Screen {

    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val db = Database(driver)
        val screenModel = rememberScreenModel { NTabelaScreenModel() }
        novatabelaScreen(navigator,screenModel,db)
    }
    @Composable
    fun novatabelaScreen(navigator: Navigator,screenModel: NTabelaScreenModel,db:Database) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        //val localDateTime = LocalDate.parse()
        val nome = remember {
            mutableStateOf("")
        }
        val data = remember {
            mutableStateOf("")
        }
        val numero = remember {
            mutableLongStateOf(0)
        }
        val dialogState = remember {
            mutableStateOf(false)
        }
        val novoState = remember {
            mutableStateOf(false)
        }
        val contState = remember {
            mutableStateOf(false)
        }
        val clientecodigo = remember {
            mutableLongStateOf(0)
        }
        val clientes = remember {
            mutableListOf<Cliente>()
        }
        val focusRequester = remember { FocusRequester() }
        Box(Modifier.fillMaxSize()){
            Row(Modifier.fillMaxSize()){
                Column(Modifier.fillMaxWidth(0.2f).fillMaxHeight().background(Color(250,255,196)), Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        bitmap = imageResource(Res.drawable.logo),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(200.dp)
                    )
                    Button(onClick = {navigator.popUntilRoot()}, Modifier.fillMaxWidth(0.9f)){
                        Text("Inicio")
                    }
                    Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                        Text("Novo Clientes")
                    }
                    Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                        Text("Ver Clientes")
                    }
                    Button(onClick = {}, Modifier.fillMaxWidth(0.9f)){
                        Text("Ver Tabelas")
                    }
                }
                Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally){
                    Row(Modifier.fillMaxWidth(0.8f),Arrangement.SpaceAround, Alignment.CenterVertically){
                        Button(onClick = {imprimir(Entregas(numero.value,nome.value,data.value))},Modifier.fillMaxWidth(0.2f)){
                            Text("Salvar e imprimir")
                        }
                        Button(onClick = {dialogState.value = salvar(Entregas(numero.value,nome.value,data.value),screenModel,db)}, Modifier.fillMaxWidth(0.2f)){
                            Text("Salvar")
                        }
                    }
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceAround,Alignment.CenterVertically){
                        OutlinedTextField(numero.value.toString(),{numero.value = it.toLongOrNull()?: 0},label = {Text("N° da entrega")},keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(nome.value,{nome.value = it},label = {Text("Nome da entrega")})
                        OutlinedTextField(data.value,{data.value = it},label = {Text("Data de Saída")})
                    }
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceAround,Alignment.CenterVertically){
                        if(contState.value){
                            Button(onClick = {novoState.value = true; clientecodigo.value = 0}, Modifier.fillMaxWidth(0.2f)){
                                Text("Adicionar Cliente")
                            }
                        }else{
                            Button(onClick = {novoState.value = true; contState.value=true;clientecodigo.value = 0;salvar(Entregas(numero.value,nome.value,data.value),screenModel,db)}, Modifier.fillMaxWidth(0.2f)){
                                Text("Adicionar Cliente")
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceAround,Alignment.CenterVertically){
                        //TODO
                        //Cadastro Tabela
                        Box(Modifier.fillMaxSize().padding(50.dp).align(Alignment.CenterVertically)){
                            val stateScroll = rememberLazyListState()
                            LazyColumn(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,state = stateScroll) {
                                item {
                                    Row(Modifier.fillMaxWidth().border(1.dp, Color.Black)){
                                        Text("Entrega", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text("Código", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text("Nome Fantasia", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text("Cidade", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text("Bairro", Modifier.padding(8.dp))
                                    }
                                }
                                var cor: Color
                                itemsIndexed(clientes){i,cliente ->
                                    val num = i+1
                                    if(num%2==0){
                                        cor = Color.LightGray
                                    }else{
                                        cor = Color.White
                                    }
                                    Row(Modifier.fillMaxWidth().background(cor)){
                                        if(num<10){
                                            Text("$num°", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        }else{
                                            Text("$num°", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        }
                                        Text(cliente.codigo.toString(), Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text(cliente.nome, Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text(cliente.cidade, Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text(cliente.bairro, Modifier.padding(8.dp))
                                    }
                                }
                            }
                            VerticalScrollbar(
                                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                adapter = rememberScrollbarAdapter(
                                    scrollState = stateScroll
                                )
                            )
                        }
                    }
                }
            }
            if (dialogState.value) {
                AlertDialog(onDismissRequest = {dialogState.value = false},
                    title = { Text("AVISO") },
                    text = { Text("Tabela Salva com Sucesso") },
                    confirmButton = {
                        Button(onClick = {dialogState.value = false}) {
                            Text("OK")
                        }
                    }
                )
            }
            if (novoState.value) {
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                AlertDialog(onDismissRequest = {novoState.value = false},
                    title = { Text("Cliente") },
                    text = {
                        OutlinedTextField(
                            clientecodigo.value.toString(),
                            {clientecodigo.value = it.toLongOrNull()?: 0},
                            label = {Text("Código do cliente")},
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {novoState.value = false;clientes.add(screenModel.addCliente(db,clientecodigo.value, (clientes.size+1).toLong(),numero.value))}),
                            modifier = Modifier.focusRequester(focusRequester)
                        )
                    },
                    confirmButton = {
                        Button(onClick = {novoState.value = false;clientes.add(screenModel.addCliente(db,clientecodigo.value, (clientes.size+1).toLong(),numero.value))}) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
    fun salvar(entrega: Entregas,screenModel: NTabelaScreenModel,db: Database):Boolean{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatteroriginal = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDateTime = LocalDate.parse(entrega.data,formatteroriginal)
        val dataf = localDateTime.format(formatter).toString()
        entrega.data = dataf
        screenModel.criarEntrega(db,entrega)
        return true;
    }
    fun imprimir(entrega: Entregas){
        var limite = 47;
        var limite_cont = 50;
        var resto = 0;
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDateTime = LocalDate.parse(entrega.data)
        val document = PDDocument()
        val info = document.documentInformation
        info.title = entrega.nome
        info.author = "Sistema de Tabelas"
        document.documentInformation = info
        val pagina = PDPage(PDRectangle.A4)
        document.addPage(pagina)
        val conteudo = PDPageContentStream(document, pagina)
        val tabela = Table.builder()
            .addColumnsOfWidth(55f,55f,150f,150f,150f)
            .fontSize(11)
            .font(PDType1Font(Standard14Fonts.getMappedFontName("Arial")))
            .wordBreak(true)
            .borderColor(JColor.BLACK)
        tabela.addRow(
            TRow.builder()
            .add(TextCell.builder()
                .text(entrega.nome+" - "+localDateTime.format(formatter))
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .colSpan(5).fontSize(20).build())
            .build()
        )
        tabela.addRow(
            TRow.builder()
            .add(TextCell.builder()
                .text("Entrega")
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .borderWidth(1f).build())
            .add(TextCell.builder().text("Código")
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .borderWidth(1f).build())
            .add(TextCell.builder().text("Nome Fantasia")
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .borderWidth(1f).build())
            .add(TextCell.builder().text("Cidade")
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .borderWidth(1f).build())
            .add(TextCell.builder().text("Bairro")
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .borderWidth(1f).build())
            .build()
        )
        var num = 1
        if(entrega.clientes.size<limite){
            limite = entrega.clientes.size
        }else{
            resto = entrega.clientes.size-limite
        }
        while(num<limite){
            val cliente = entrega.clientes[num-1]
            var cor = JColor.WHITE
            if(num%2==1){
                cor = JColor.LIGHT_GRAY
            }
            tabela.addRow(
                TRow.builder()
                .add(TextCell.builder().text("$num°")
                    .horizontalAlignment(HorizontalAlignment.LEFT)
                    .borderWidth(1f).build())
                .add(TextCell.builder().text("${cliente.codigo}")
                    .horizontalAlignment(HorizontalAlignment.CENTER)
                    .borderWidth(1f).build())
                .add(TextCell.builder().text(cliente.nome)
                    .horizontalAlignment(HorizontalAlignment.CENTER)
                    .borderWidth(1f).build())
                .add(TextCell.builder().text(cliente.cidade)
                    .horizontalAlignment(HorizontalAlignment.CENTER)
                    .borderWidth(1f).build())
                .add(TextCell.builder().text(cliente.bairro)
                    .horizontalAlignment(HorizontalAlignment.CENTER)
                    .borderWidth(1f).build())
                .backgroundColor(cor)
                .build()
            )
            num++
        }
        val desenhar = TableDrawer.builder()
            .contentStream(conteudo)
            .startX(20f)
            .startY(pagina.getMediaBox().getUpperRightY() - 20f)
            .table(tabela.build())
            .build()
        desenhar.draw()
        conteudo.close()
        while(resto>0){
            var cont = 0
            if(resto<=limite_cont){
                limite_cont = resto+1
            }
            val pagina2 = PDPage(PDRectangle.A4)
            document.addPage(pagina2)
            val conteudo2 = PDPageContentStream(document, pagina2)
            val tabela2 = Table.builder()
                .addColumnsOfWidth(55f,55f,150f,150f,150f)
                .fontSize(11)
                .font(PDType1Font(Standard14Fonts.getMappedFontName("Arial")))
                .wordBreak(true)
                .borderColor(_root_ide_package_.java.awt.Color.BLACK)
            while(cont<limite_cont){
                val cliente = entrega.clientes[num-1]
                var cor = JColor.WHITE
                if(num%2==1){
                    cor = JColor.LIGHT_GRAY
                }
                tabela2.addRow(
                    TRow.builder()
                    .add(TextCell.builder().text("$num°")
                        .horizontalAlignment(HorizontalAlignment.LEFT)
                        .borderWidth(1f).build())
                    .add(TextCell.builder().text("${cliente.codigo}")
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .borderWidth(1f).build())
                    .add(TextCell.builder().text(cliente.nome)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .borderWidth(1f).build())
                    .add(TextCell.builder().text(cliente.cidade)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .borderWidth(1f).build())
                    .add(TextCell.builder().text(cliente.bairro)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .borderWidth(1f).build())
                    .backgroundColor(cor)
                    .build()
                )
                num++
                cont++
            }
            val desenhar2 = TableDrawer.builder()
                .contentStream(conteudo2)
                .startX(20f)
                .startY(pagina.getMediaBox().getUpperRightY() - 20f)
                .table(tabela2.build())
                .build()
            desenhar2.draw()
            conteudo2.close()
            resto -= limite_cont
        }
        val job = PrinterJob.getPrinterJob()
        job.setPageable(PDFPageable(document))
        val att = HashPrintRequestAttributeSet()
        att.add(Sides.TWO_SIDED_LONG_EDGE);
        if(job.printDialog(att)){
            job.print(att)
        }
        document.close()
    }
}