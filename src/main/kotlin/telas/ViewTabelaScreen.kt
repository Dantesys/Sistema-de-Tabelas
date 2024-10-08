package telas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import data.Entregas
import telas.parts.loadingContent
import models.ViewTabelaScreenModel
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
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.Sides
import java.awt.Color as JColor

class ViewTabelaScreen(val driver: SqlDriver, val id:Long) : Screen {

    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val db = Database(driver)
        val screenModel = rememberScreenModel { ViewTabelaScreenModel() }
        val state by screenModel.state.collectAsState()
        when (val result = state) {
            is ViewTabelaScreenModel.State.Loading -> loadingContent(navigator,true,driver)
            is ViewTabelaScreenModel.State.Result -> vertabelaScreen(result.entrega,navigator)
        }
        LaunchedEffect(currentCompositeKeyHash){
            screenModel.getClientes(db,id)
        }
    }
    @Composable
    fun vertabelaScreen(entrega: Entregas,navigator: Navigator) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDateTime = LocalDate.parse(entrega.data)
        val dialogState = remember { mutableStateOf(false) }
        Box(Modifier.fillMaxSize()){
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
                        IconButton(onClick = {imprimir(entrega)}){
                            Column(horizontalAlignment = Alignment.CenterHorizontally){
                                Text("Imprimir")
                                Icon(imageVector =  Icons.Default.Print,"icone de imprensão")
                            }
                        }
                        Text(entrega.nome+" - "+localDateTime.format(formatter).toString(), style = TextStyle(fontSize = 30.sp))
                        IconButton(onClick = {dialogState.value = gerarPDF(entrega)}){
                            Column(horizontalAlignment = Alignment.CenterHorizontally){
                                Text("Salvar PDF")
                                Icon(imageVector =  Icons.Default.PictureAsPdf,"icone de salvar")
                            }
                        }
                        if(entrega.pedencia){
                            IconButton(onClick = {navigator.push(EditTabelaScreen(driver,id))}){
                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                    Text("Editar")
                                    Icon(imageVector =  Icons.Default.Edit,"icone de editar")
                                }
                            }
                        }
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
                                        Text("Bairro", Modifier.padding(8.dp))
                                    }
                                }
                                var cor: Color
                                itemsIndexed(entrega.clientes){i,cliente ->
                                    val num = i+1
                                    cor = if(num%2==0){
                                        Color.LightGray
                                    }else{
                                        Color.White
                                    }
                                    Row(Modifier.fillMaxWidth().background(cor)){
                                        if(num<10){
                                            Text("0$num°", Modifier.fillMaxWidth(0.15f).padding(8.dp))
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
                    text = {Text("PDF Salvo com Sucesso")},
                    confirmButton = {
                        Button(onClick = {dialogState.value = false}) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
    private fun gerarPDF(entrega:Entregas):Boolean{
        File("./tabelas").mkdir()
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
            .add(TextCell.builder().text(entrega.nome+" - "+localDateTime.format(formatter))
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
                .add(TextCell.builder().text(cliente.nome).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                .add(TextCell.builder().text(cliente.cidade).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                .add(TextCell.builder().text(cliente.bairro).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
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
                    .add(TextCell.builder().text(cliente.nome).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                    .add(TextCell.builder().text(cliente.cidade).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                    .add(TextCell.builder().text(cliente.bairro).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
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
        document.save("./tabelas/"+entrega.nome+"-"+entrega.data+".pdf")
        document.close()
        return true
    }
    private fun imprimir(entrega:Entregas){
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
            .add(TextCell.builder().text(entrega.nome+" - "+localDateTime.format(formatter))
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
                .add(TextCell.builder().text(cliente.nome).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                .add(TextCell.builder().text(cliente.cidade).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                .add(TextCell.builder().text(cliente.bairro).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
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
                    .add(TextCell.builder().text(cliente.nome).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                    .add(TextCell.builder().text(cliente.cidade).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                    .add(TextCell.builder().text(cliente.bairro).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
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
}