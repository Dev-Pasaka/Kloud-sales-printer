import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import data.repository.ReceiptRepositoryImpl
import domain.usecase.GenerateReceiptUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.ImageType.*
import org.apache.pdfbox.rendering.PDFRenderer
import java.awt.image.BufferedImage
import java.io.*
import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.Copies





fun printTextFile(filePath: String) {
    val file = File(filePath)
    if (!file.exists()) {
        println("File not found: $filePath")
        return
    }


    val printRequestAttributeSet = HashPrintRequestAttributeSet().apply {
        add(Copies(1))
    }

    val printService = PrintServiceLookup.lookupDefaultPrintService() ?: run {
        println("No default printer found.")
        return
    }

    val docPrintJob: DocPrintJob? = printService.createPrintJob()

    val flavor = DocFlavor.INPUT_STREAM.TEXT_PLAIN_HOST
    val doc: Doc = SimpleDoc(FileInputStream(file), flavor, null)

    docPrintJob?.print(doc, printRequestAttributeSet)
}

fun printPNGImage(filePath: String): Flow<Pair<String, Boolean?>> = flow {
    val file = File(filePath)
    if (!file.exists()) {
        emit(Pair("File not found: $filePath", false))
        return@flow
    }

    emit(Pair("File found: $filePath", null))

    val printRequestAttributeSet = HashPrintRequestAttributeSet().apply {
        add(Copies(1))
    }

    val printService = PrintServiceLookup.lookupDefaultPrintService() ?: run {
        emit(Pair("No default printer found.", false))
        return@flow
    }

    emit(Pair("Default printer found: ${printService.name}", null))

    val docPrintJob: DocPrintJob = printService.createPrintJob()

    val flavor = DocFlavor.INPUT_STREAM.PNG
    val doc: Doc = SimpleDoc(file.inputStream(), flavor, null)

    try {
        docPrintJob.print(doc, printRequestAttributeSet)
        emit(Pair("Printing started.", null))
        emit(Pair("Printing completed successfully.", true))
    } catch (e: PrintException) {
        emit(Pair("Printing failed: ${e.message}", false))
    }
}


suspend fun main() {



    printPNGImage("./receipt_with_qr.png").collect{
        println(it)
    }
}


val formattedContent = """
        {Ubuniworks Ltd}:
        P.O BOX 16779 00100
        Nairobi, Kenya
        
        {Receipt}:
        Order Type : Order Out
        Receipt No : 686
        Customer/Table : none
        Waiter : Shadrack Kristian
        Station : Test Station
        
        {Product(s)}:
        1 Honey (KES 1)  50 pcs  = KES 50
        Total : KES 50.0
        
        Amount Paid : KES 50
        Balance : KES 0
        Payment Method : Cash
        
        {Etims Information}:
        SCU ID : Pending
        Invoice No: Pending
        Internal Data : Pending
        Receipt Signature : Pending
        
        Thank you
        Printed on : Fri, 7 Jun 2024 :21:00:52
        Powered by Ubuniworks
        KloudSales - POS
        Email : info@ubuniworks.com
        Phone : +254717722324
        """.trimIndent()