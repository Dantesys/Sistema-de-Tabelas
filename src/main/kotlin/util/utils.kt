package util

import com.dantesys.Cliente
import com.dantesys.Entrega
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.apache.pdfbox.printing.PDFPageable
import org.vandeseer.easytable.TableDrawer
import org.vandeseer.easytable.settings.HorizontalAlignment
import org.vandeseer.easytable.structure.Row
import org.vandeseer.easytable.structure.Table
import org.vandeseer.easytable.structure.cell.TextCell
import java.awt.Color
import java.awt.print.PrinterJob
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.Sides

fun gerarDoc(entrega:Entrega,clientes:List<Cliente>):PDDocument{
    var limite = 47
    var limiteCont = 50
    var resto = 0
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val localDateTime = LocalDate.parse(entrega.data_)
    val document = PDDocument()
    val info = document.documentInformation
    info.title = entrega.nome
    info.author = "Sistema de Tabelas"
    document.documentInformation = info
    val pagina = PDPage(PDRectangle.A4)
    document.addPage(pagina)
    val conteudo = PDPageContentStream(document,pagina)
    val tabela = Table.builder()
        .addColumnsOfWidth(23f,55f,157f,157f,157f)
        .fontSize(11)
        .font(PDType1Font(Standard14Fonts.getMappedFontName("Arial")))
        .wordBreak(true)
        .borderColor(Color.BLACK)
    tabela.addRow(
        Row.builder()
            .add(
                TextCell.builder().text(entrega.nome.uppercase()+" - "+localDateTime.format(formatter))
                    .horizontalAlignment(HorizontalAlignment.CENTER)
                    .colSpan(5)
                    .fontSize(20)
                    .build())
            .build()
    )
    tabela.addRow(
        Row.builder()
            .add(TextCell.builder().text("Entrega").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
            .add(TextCell.builder().text("Código").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
            .add(TextCell.builder().text("Nome Fantasia").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
            .add(TextCell.builder().text("Cidade").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
            .add(TextCell.builder().text("Bairro").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
            .build()
    )
    var num = 1
    if(clientes.size<limite){
        limite = clientes.size
    }else{
        resto = clientes.size-limite
    }
    while(num<limite){
        val cliente = clientes[num-1]
        var cor = Color.WHITE
        if(num%2==1){
            cor = Color.LIGHT_GRAY
        }
        tabela.addRow(
            Row.builder()
                .add(TextCell.builder().text("$num°").horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1f).build())
                .add(TextCell.builder().text("${cliente.codigo}").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                .add(cliente.nome?.let { TextCell.builder().text(it.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build() })
                .add(cliente.cidade?.let { TextCell.builder().text(it.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build() })
                .add(cliente.bairro?.let { TextCell.builder().text(it.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build() })
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
            .addColumnsOfWidth(23f,55f,157f,157f,157f)
            .fontSize(11)
            .font(PDType1Font(Standard14Fonts.getMappedFontName("Arial")))
            .wordBreak(true)
            .borderColor(Color.BLACK)
        while(cont<limiteCont){
            val cliente = clientes[num-1]
            var cor = Color.WHITE
            if(num%2==1){
                cor = Color.LIGHT_GRAY
            }
            tabela2.addRow(
                Row.builder()
                    .add(TextCell.builder().text("$num°").horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1f).build())
                    .add(TextCell.builder().text("${cliente.codigo}").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
                    .add(cliente.nome?.let { TextCell.builder().text(it.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build() })
                    .add(cliente.cidade?.let { TextCell.builder().text(it.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build() })
                    .add(cliente.bairro?.let { TextCell.builder().text(it.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build() })
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
    return document
}
fun imprimir(entrega: Entrega, clientes:List<Cliente>){
    val document = gerarDoc(entrega,clientes)
    val job = PrinterJob.getPrinterJob()
    job.setPageable(PDFPageable(document))
    val att = HashPrintRequestAttributeSet()
    att.add(Sides.TWO_SIDED_LONG_EDGE)
    if(job.printDialog(att)){
        job.print(att)
    }
    document.close()
}
fun gerarPDF(entrega: Entrega,clientes:List<Cliente>):Boolean{
    File("./tabelas").mkdir()
    val document = gerarDoc(entrega,clientes)
    document.save("./tabelas/"+entrega.nome+"-"+entrega.data_+".pdf")
    document.close()
    return true
}
fun Long.toBrazilianDateFormat(pattern: String = "dd/MM/yyyy"):String {
    val date = Date(this)
    val formatter = SimpleDateFormat(pattern).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }
    return formatter.format(date)
}