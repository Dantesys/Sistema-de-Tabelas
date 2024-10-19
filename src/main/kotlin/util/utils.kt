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
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val localDateTime = LocalDate.parse(entrega.data_)
    val document = PDDocument()
    val info = document.documentInformation
    info.title = entrega.nome
    info.author = "Sistema de Tabelas"
    document.documentInformation = info
    var pagina = PDPage(PDRectangle.A4)
    document.addPage(pagina)
    var conteudo = PDPageContentStream(document,pagina)
    var tabela = Table.builder()
        .addColumnsOfWidth(30f,55f,155f,155f,155f)
        .fontSize(9)
        .font(PDType1Font(Standard14Fonts.getMappedFontName("Arial")))
        .wordBreak(true)
        .borderColor(Color.BLACK)
    val trow = Row.builder()
        .add(TextCell.builder().text(entrega.nome.uppercase()+" - "+localDateTime.format(formatter))
        .horizontalAlignment(HorizontalAlignment.CENTER)
        .colSpan(5)
        .fontSize(20)
        .build())
        .build()
    tabela.addRow(trow)
    val frow = Row.builder()
        .add(TextCell.builder().text("Nº").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
        .add(TextCell.builder().text("Código").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
        .add(TextCell.builder().text("Nome Fantasia").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
        .add(TextCell.builder().text("Cidade").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
        .add(TextCell.builder().text("Bairro").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
        .build()
    tabela.addRow(frow)
    var num = 1
    for(cliente in clientes){
        var cor = Color.WHITE
        if(num%2==1){
            cor = Color.LIGHT_GRAY
        }
        val temp = tabela
        val height = temp.build().height+60f
        if(height>=PDRectangle.A4.height || height>=pagina.mediaBox.height){
            val desenhar = TableDrawer.builder()
                .contentStream(conteudo)
                .startX(20f)
                .startY(pagina.mediaBox.upperRightY - 20f)
                .table(tabela.build())
                .build()
            desenhar.draw()
            conteudo.close()
            pagina = PDPage(PDRectangle.A4)
            document.addPage(pagina)
            conteudo = PDPageContentStream(document,pagina)
            tabela = Table.builder()
                .addColumnsOfWidth(30f,55f,155f,155f,155f)
                .fontSize(9)
                .font(PDType1Font(Standard14Fonts.getMappedFontName("Arial")))
                .wordBreak(true)
                .borderColor(Color.BLACK)
        }
        val row = Row.builder()
            .add(TextCell.builder().text("$num°").horizontalAlignment(HorizontalAlignment.LEFT).borderWidth(1f).wordBreak(false).build())
            .add(TextCell.builder().text("${cliente.codigo}").horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build())
            .add(cliente.nome?.let { TextCell.builder().text(it.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build() })
            .add(cliente.cidade?.let { TextCell.builder().text(it.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build() })
            .add(cliente.bairro?.let { TextCell.builder().text(it.uppercase()).horizontalAlignment(HorizontalAlignment.CENTER).borderWidth(1f).build() })
            .backgroundColor(cor)
            .build()
        tabela.addRow(row)
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