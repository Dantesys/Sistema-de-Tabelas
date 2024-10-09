package telas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.sqldelight.db.SqlDriver
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dantesys.Database
import data.Cliente
import data.Entregas
import models.EditTabelaScreenModel
import telas.parts.loadingContent
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.apache.pdfbox.printing.PDFPageable
import org.vandeseer.easytable.TableDrawer
import org.vandeseer.easytable.settings.HorizontalAlignment
import org.vandeseer.easytable.structure.Table
import org.vandeseer.easytable.structure.Row as TRow
import org.vandeseer.easytable.structure.cell.TextCell
import telas.parts.menu
import java.awt.print.PrinterJob
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.Sides
import java.awt.Color as JColor

class EditTabelaScreen(val driver: SqlDriver, val id:Long) : Screen {

    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val db = Database(driver)
        val screenModel = rememberScreenModel { EditTabelaScreenModel() }
        val state by screenModel.state.collectAsState()
        when (val result = state) {
            is EditTabelaScreenModel.State.Loading -> loadingContent(navigator,true,driver)
            is EditTabelaScreenModel.State.Result -> edittabelaScreen(result.entrega,navigator,screenModel,db)
        }
        LaunchedEffect(currentCompositeKeyHash){
            screenModel.getClientes(db,id)
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun edittabelaScreen(entrega: Entregas,navigator: Navigator,screenModel:EditTabelaScreenModel,db:Database) {
        val dialogState = remember { mutableStateOf(false) }
        val editState = remember { mutableStateOf(false) }
        val cNome = remember { mutableStateOf("") }
        val cCidade = remember { mutableStateOf("") }
        val cBairro = remember { mutableStateOf("") }
        val cCodigo = remember { mutableLongStateOf(0) }
        val eCliente = remember { mutableStateOf(Cliente(0)) }
        val nome = remember { mutableStateOf(entrega.nome) }
        val focusManager = LocalFocusManager.current
        var showDatePickerDialog by remember {mutableStateOf(false)}
        val datePickerState = rememberDatePickerState()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDateTime = LocalDate.parse(entrega.data)
        val data = remember { mutableStateOf(localDateTime.format(formatter)) }
        Box(Modifier.fillMaxSize()){
            if (showDatePickerDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDatePickerDialog = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                datePickerState
                                    .selectedDateMillis?.let { millis ->
                                        data.value = millis.toBrazilianDateFormat()
                                    }
                                showDatePickerDialog = false
                            }) {
                            Text(text = "Escolher data")
                        }
                    }) {
                    DatePicker(state = datePickerState)
                }
            }
            Row(Modifier.fillMaxSize()){
                menu(Modifier.fillMaxWidth(0.2f).fillMaxHeight().background(Color(250,255,196)),
                    Arrangement.spacedBy(10.dp),
                    Alignment.CenterHorizontally,
                    navigator,
                    driver,
                    false
                )
                Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally){
                    Row(Modifier.fillMaxWidth(0.8f),Arrangement.SpaceAround, Alignment.CenterVertically){
                        IconButton(onClick = {entrega.nome=nome.value;entrega.data=localDateTime.format(formatter2);dialogState.value = imprimir(entrega,screenModel,db)}){
                            Column(horizontalAlignment = Alignment.CenterHorizontally){
                                Text("Salvar e imprimir")
                                Icon(imageVector =  Icons.Default.Print,"icone de imprensão")
                            }
                        }
                        Text("Entrega Nº"+entrega.id.toString(), style = TextStyle(fontSize = 30.sp))
                        IconButton(onClick = {entrega.nome=nome.value;entrega.data=localDateTime.format(formatter2);dialogState.value = salvar(entrega,screenModel,db)}){
                            Column(horizontalAlignment = Alignment.CenterHorizontally){
                                Text("Salvar")
                                Icon(imageVector =  Icons.Default.Save,"icone de salvar")
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceAround,Alignment.CenterVertically){
                        OutlinedTextField(nome.value,{nome.value = it},label = {Text("Nome da entrega")})
                        OutlinedTextField(data.value,{}, modifier = Modifier.onFocusEvent {
                            if (it.isFocused) {
                                showDatePickerDialog = true
                                focusManager.clearFocus(force = true)
                            }
                        },label = {Text("Data de Saída")},readOnly = true)
                    }
                    Row(Modifier.fillMaxWidth(),Arrangement.SpaceAround, Alignment.CenterVertically){
                        Box(Modifier.fillMaxSize().padding(50.dp).align(Alignment.CenterVertically)){
                            val state = rememberLazyListState()
                            LazyColumn(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,state = state) {
                                item {
                                    Row(Modifier.fillMaxWidth().border(1.dp, Color.Black)){
                                        Text("Entrega", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text("Código", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text("Nome Fantasia", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text("Cidade", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text("Bairro", Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text("Editar", Modifier.padding(8.dp))
                                    }
                                }
                                var cor: Color
                                itemsIndexed(entrega.clientes){i,cliente ->
                                    var p = true
                                    if(cliente.nome != "" && cliente.cidade != "" && cliente.bairro != ""){
                                        p=false
                                    }
                                    val num = i+1
                                    cor = if(num%2==0){
                                        Color.LightGray
                                    }else{
                                        Color.White
                                    }
                                    Row(Modifier.fillMaxWidth().background(cor)){
                                        Text("$num°", Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text(cliente.codigo.toString(), Modifier.fillMaxWidth(0.15f).padding(8.dp))
                                        Text(cliente.nome.uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text(cliente.cidade.uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        Text(cliente.bairro.uppercase(), Modifier.fillMaxWidth(0.4f).padding(8.dp))
                                        if(p){
                                            IconButton(onClick = {eCliente.value=cliente;cCodigo.value=cliente.codigo;cNome.value=cliente.nome;cCidade.value=cliente.cidade;cBairro.value=cliente.bairro;editState.value=true}){
                                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                                    Icon(imageVector =  Icons.Default.Edit,"icone de editar")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            VerticalScrollbar(
                                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                adapter = rememberScrollbarAdapter(
                                    scrollState = state
                                )
                            )
                        }
                    }
                }
            }
            if (dialogState.value) {
                AlertDialog(onDismissRequest = {dialogState.value = false},
                    title = {Text("AVISO")},
                    text = {Text("Salvo com Sucesso")},
                    confirmButton = {
                        Button(onClick = {dialogState.value = false;navigator.popUntil { it==ViewTabelaScreen::class }}) {
                            Text("OK")
                        }
                    }
                )
            }
            if(editState.value){
                AlertDialog(onDismissRequest = {editState.value = false},
                    title = {Text("EDITAR")},
                    text = {
                        Column{
                            Text("Código: "+cCodigo.value.toString())
                            OutlinedTextField(cNome.value,{cNome.value = it},label = {Text("Nome Fantasia")})
                            OutlinedTextField(cCidade.value,{cCidade.value = it},label = {Text("Cidade")})
                            OutlinedTextField(cBairro.value,{cBairro.value = it},label = {Text("Bairro")})
                        }
                    },
                    dismissButton = {
                        Button(onClick = {editState.value = false}) {
                            Text("Cancelar")
                        }
                    },
                    confirmButton = {
                        Button(onClick = {eCliente.value.nome=cNome.value;eCliente.value.cidade=cCidade.value;eCliente.value.bairro=cBairro.value;screenModel.editCliente(db,eCliente.value);editState.value = false;navigator.push(EditTabelaScreen(driver,id))}) {
                            Text("Salvar")
                        }
                    }
                )
            }
        }
    }
    private fun salvar(entrega: Entregas,screenModel: EditTabelaScreenModel,db:Database):Boolean{
        screenModel.criarEntrega(db,entrega)
        return true
    }
    private fun imprimir(entrega:Entregas,screenModel: EditTabelaScreenModel,db:Database):Boolean{
        if(salvar(entrega,screenModel,db)){
            var limite = 47
            var limiteCont = 50
            var resto = 0
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val localDateTime = LocalDate.parse(entrega.data)
            val document = PDDocument()
            val info = document.documentInformation
            info.title = entrega.nome
            info.author = "Sistema de Tabelas"
            document.documentInformation = info
            val pagina = PDPage(PDRectangle.A4)
            document.addPage(pagina)
            val conteudo = PDPageContentStream(document,pagina)
            val tabela = Table.builder()
                .addColumnsOfWidth(55f,55f,150f,150f,150f)
                .fontSize(11)
                .font(PDType1Font(Standard14Fonts.getMappedFontName("Arial")))
                .wordBreak(true)
                .borderColor(JColor.BLACK)
            tabela.addRow(TRow.builder()
                .add(TextCell.builder().text(entrega.nome.uppercase()+" - "+localDateTime.format(formatter))
                    .horizontalAlignment(HorizontalAlignment.CENTER)
                    .colSpan(5)
                    .fontSize(20)
                    .build())
                .build()
            )
            tabela.addRow(TRow.builder()
                .add(TextCell.builder().text("Entrega").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                .add(TextCell.builder().text("Código").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                .add(TextCell.builder().text("Nome Fantasia").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                .add(TextCell.builder().text("Cidade").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                .add(TextCell.builder().text("Bairro").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
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
                tabela.addRow(TRow.builder()
                    .add(TextCell.builder().text("$num°").horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1f).build())
                    .add(TextCell.builder().text("${cliente.codigo}").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                    .add(TextCell.builder().text(cliente.nome.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                    .add(TextCell.builder().text(cliente.cidade.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                    .add(TextCell.builder().text(cliente.bairro.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                    .backgroundColor(cor)
                    .build()
                )
                num++
            }
            val desenhar = TableDrawer.builder()
                .contentStream(conteudo)
                .startX(20f)
                .startY(pagina.mediaBox.upperRightY - 20f)
                .table(tabela.build())
                .build()
            desenhar.draw()
            conteudo.close()
            while(resto>0){
                var cont = 0
                if(resto<=limiteCont){
                    limiteCont = resto+1
                }
                val pagina2 = PDPage(PDRectangle.A4)
                document.addPage(pagina2)
                val conteudo2 = PDPageContentStream(document,pagina2)
                val tabela2 = Table.builder()
                    .addColumnsOfWidth(55f,55f,150f,150f,150f)
                    .fontSize(11)
                    .font(PDType1Font(Standard14Fonts.getMappedFontName("Arial")))
                    .wordBreak(true)
                    .borderColor(JColor.BLACK)
                while(cont<limiteCont){
                    val cliente = entrega.clientes[num-1]
                    var cor = JColor.WHITE
                    if(num%2==1){
                        cor = JColor.LIGHT_GRAY
                    }
                    tabela2.addRow(TRow.builder()
                        .add(TextCell.builder().text("$num°").horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1f).build())
                        .add(TextCell.builder().text("${cliente.codigo}").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                        .add(TextCell.builder().text(cliente.nome.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                        .add(TextCell.builder().text(cliente.cidade.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                        .add(TextCell.builder().text(cliente.bairro.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                        .backgroundColor(cor)
                        .build()
                    )
                    num++
                    cont++
                }
                val desenhar2 = TableDrawer.builder()
                    .contentStream(conteudo2)
                    .startX(20f)
                    .startY(pagina.mediaBox.upperRightY - 20f)
                    .table(tabela2.build())
                    .build()
                desenhar2.draw()
                conteudo2.close()
                resto -= limiteCont
            }
            val job = PrinterJob.getPrinterJob()
            job.setPageable(PDFPageable(document))
            val att = HashPrintRequestAttributeSet()
            att.add(Sides.TWO_SIDED_LONG_EDGE)
            if(job.printDialog(att)){
                job.print(att)
            }
            document.close()
        }
        return true
    }
    private fun Long.toBrazilianDateFormat(
        pattern: String = "dd/MM/yyyy"
    ): String {
        val date = Date(this)
        val formatter = SimpleDateFormat(pattern).apply {
            timeZone = TimeZone.getTimeZone("GMT")
        }
        return formatter.format(date)
    }
}