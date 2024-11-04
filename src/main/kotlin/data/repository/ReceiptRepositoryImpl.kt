package data.repository

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

import data.remote.request.GetReceiptsReq
import data.remote.response.getReceiptsRes.GetReceiptsResItem
import data.remote.response.getZReport.GetZreportRes
import domain.repository.ReceiptRepository
import gui.ava.html.image.generator.HtmlImageGenerator
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import jpos.JposException
import jpos.POSPrinter
import jpos.POSPrinterConst
import jpos.util.JposProperties
import kotlinx.coroutines.*
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.printing.PDFPrintable
import org.apache.pdfbox.rendering.PDFRenderer
import org.xhtmlrenderer.pdf.ITextFontResolver
import org.xhtmlrenderer.pdf.ITextRenderer
import org.xhtmlrenderer.swing.Java2DRenderer
import utils.KeyValueStorage
import utils.KtorClient
import java.awt.*
import java.awt.image.BufferedImage
import java.awt.print.*
import java.awt.print.Printable.NO_SUCH_PAGE
import java.awt.print.Printable.PAGE_EXISTS
import java.io.*
import java.net.URL
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO
import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.*
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.text.html.HTMLDocument
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.StyleSheet
import kotlin.time.Duration.Companion.seconds


class ReceiptRepositoryImpl() : ReceiptRepository {
    // Utility to get desktop path for receipts folder
    fun getReceiptsFolderPath(): String {
        return Paths.get(System.getProperty("user.home"), "Desktop", "receipts").toString()
    }

    private fun ensureReceiptsFolderExists() {
        val receiptsFolder = Paths.get(getReceiptsFolderPath()).toFile()
        if (!receiptsFolder.exists()) {
            receiptsFolder.mkdirs()  // Create the folder if it doesn't exist
        }
    }

    // Utility function to ensure the directory exists
    private fun ensureDirectoryExists(directoryPath: String) {
        val directory = File(directoryPath)
        if (!directory.exists()) {
            directory.mkdirs() // Create the directory if it doesn't exist
        }
    }


    private fun generateQRCodeImage(link: String, size: Int, receiptId: String) {
        // Define the main receipts folder and the subdirectory
        val receiptsFolderPath = Paths.get(getReceiptsFolderPath(), "qr_code_images").toString()

        // Ensure the receipts subdirectory exists
        ensureDirectoryExists(receiptsFolderPath)

        // Define the file path where the QR code will be saved
        val filePath = Paths.get(receiptsFolderPath, "qr-code-image.png").toString()

        // Set encoding hints for the QR code
        val hints = mutableMapOf<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

        // Generate the QR code
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(link, BarcodeFormat.QR_CODE, size, size, hints)

        // Create an image from the generated QR code
        val bufferedImage = BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bufferedImage.setRGB(x, y, if (bitMatrix[x, y]) Color.BLACK.rgb else Color.WHITE.rgb)
            }
        }

        // Save the QR code image to the specified file path
        ImageIO.write(bufferedImage, "png", File(filePath))
    }


    suspend fun generateImage(html: String, receiptId: String) {
        // Ensure the receipts folder exists
        val receiptsFolderPath = Paths.get(getReceiptsFolderPath(), "receipts-with-qr").toString()

        // Ensure the receipts subdirectory exists
        ensureDirectoryExists(receiptsFolderPath)

        // Create an instance of HtmlImageGenerator
        val imageGenerator = HtmlImageGenerator()

        // Load HTML content
        imageGenerator.loadHtml(html)

        // Set the size for the image
        imageGenerator.setSize(Dimension(100, imageGenerator.size.height))

        // Save the image as PNG to the desktop's receipts folder
        val desktopReceiptsPath =
            Paths.get(getReceiptsFolderPath(), "receipts-with-qr/${receiptId}_receipt.png").toString()
        imageGenerator.saveAsImage(desktopReceiptsPath)
        val file = File(desktopReceiptsPath)
       printPNGImage(desktopReceiptsPath)

    }

    suspend fun generateZReportImage(html: String) {
        // Ensure the receipts folder exists
        val receiptsFolderPath = Paths.get(getReceiptsFolderPath(), "ZReports").toString()

        // Ensure the receipts subdirectory exists
        ensureDirectoryExists(receiptsFolderPath)

        // Create an instance of HtmlImageGenerator
        val imageGenerator = HtmlImageGenerator()

        // Load HTML content
        imageGenerator.loadHtml(html)

        // Set the size for the image
        imageGenerator.setSize(Dimension(400, imageGenerator.size.height))

        // Save the image as PNG to the desktop's receipts folder
        val desktopReceiptsPath =
            Paths.get(getReceiptsFolderPath(), "ZReports/${System.currentTimeMillis()}_zreport.png").toString()
        imageGenerator.saveAsImage(desktopReceiptsPath)
        val file = File(desktopReceiptsPath)
        //printPNGImage(desktopReceiptsPath)

    }

    override suspend fun printPNGImage(filePath: String): Pair<String, Boolean?> = withContext(Dispatchers.IO) {
        val file = File(filePath)
        if (!file.exists()) {
            return@withContext Pair("File not found: $filePath", false)
        }
        println("Printing receipt: ${file.path}")
        val printRequestAttributeSet = HashPrintRequestAttributeSet().apply {
            add(Copies(1))
            add(MediaSizeName.ISO_A4) // Ensure the correct paper size is set
            add(OrientationRequested.PORTRAIT) // Set the correct orientation
        }

        val printService = PrintServiceLookup.lookupDefaultPrintService() ?: run {
            return@withContext Pair("No default printer found.", false)
        }

        val docPrintJob: DocPrintJob = printService.createPrintJob()
        val flavor = DocFlavor.INPUT_STREAM.PNG
        val doc: Doc = SimpleDoc(file.inputStream(), flavor, null)

        try {
            docPrintJob.print(doc, printRequestAttributeSet)
            println("Printing started " + filePath)
            return@withContext Pair("Printing completed successfully.", true)
        } catch (e: PrintException) {
            e.printStackTrace()
            return@withContext Pair("Printing failed: ${e.message}", false)
        }
    }


    // Updated function to generate the formatted receipt string
    override fun convertJsonToFormattedReceiptString(receipt: GetReceiptsResItem): String {
        val receiptType = if (receipt.status == "pending") "BILL" else "RECEIPT"

        // Define the path where the QR code image will be saved
        val qrCodeFilePath = "qr_code_image.png" // Adjust the path as necessary

        // Generate and save the QR code image
        generateQRCodeImage(receipt.qrurl ?: "No data", 170, qrCodeFilePath) // Reduced size to 100x100 for QR code

        val itemsHtml = receipt.items.joinToString("") { item ->
            val itemTotal = item.price.toDouble() * item.qty.toDouble()
            """
        <tr>
            <td>${item.name} @${item.price}</td>
            <td class="right-align">X${item.qty}</td>
            <td class="right-align">${itemTotal}0</td>
        </tr>
        """.trimIndent()
        }

        val totalAmount = receipt.total_amount.toDouble()
        val amountPaid = totalAmount // Adjust this as necessary
        val balance = amountPaid - totalAmount

        val footerHtml = if (receipt.status != "pending") {
            """
        <p><b>Total Amount: KES %.2f</b></p>
        <p>Amount Paid: KES %.2f</p>
        <p>Balance: KES %.2f</p>
        """.trimIndent().format(totalAmount, amountPaid, balance)
        } else {
            ""
        }

        val qrCode = Paths.get(getReceiptsFolderPath(), "qr_code_images", "qr-code-image.png").toString()

        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Receipt</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    width: 400px; /* Reduced width for compactness */
                    font-size: 16px; /* Smaller font size */
                    margin: 0 auto;
                    padding: 5px;
                    line-height: 1.1; /* Reduced line spacing */
                }
                .header, .kra, .footer {
                    line-height: 1.1; /* Reduced line height */
                    font-size: 16px; /* Compact font */
                }
              
                h2, p {
                    margin: 0;
                    padding: 0;
                }
                .line, .straight-line {
                    margin: 2px 0;
                }
                .line {
                    border-top: 1px dashed #000;
                }
                .straight-line {
                    border-top: 1px solid #000;
                }
                .item-table {
                    width: 100%;
                    border-collapse: collapse;
                }
                .item-table th, .item-table td {
                    padding: 2px; /* Minimal padding for table cells */
                    text-align: left;
                }
                .item-table th {
                    border-bottom: 1px solid #000;
                }
                .right-align {
                    text-align: right;
                }
                img {
                    width: 65px; /* Increased QR code size by 30% */
                    height: 65px;
                    margin: 5px 0;
                }
                .footer p {
                    margin: 0px 0;
                    font-size: 14px; /* Slightly smaller footer text */
                }
            </style>
        </head>
        <body>
            <div class="header">
                <p>Ubuniworks Ltd<br>P.O BOX 16779 00100<br>Nairobi, Kenya</p>
            </div>
            <br>
            <h2 style="font-size: 20px;">${receiptType.padEnd(50)}</h2>
            <br>
            <p>${if (receipt.status == "pending") "BILL No: ${receipt.id}" else "Receipt No: ${receipt.id}"}</p>
            <p>Station: ${receipt.placing_station.name}</p>
            <p>Order Type: ${receipt.type}</p>
            <p>Waiter: ${receipt.placing_waiter?.name ?: "N/A"}</p>
            <p>Table No./Cust Name: ${receipt.customer}</p>
            <br>
            
            <table class="item-table">
                <tr>
                    <th>Items</th>
                    <th>Qty</th>
                    <th>Total</th>
                </tr>
                $itemsHtml
            </table>
            <div class="straight-line"></div>
            $footerHtml
            ${if (receipt.status == "paid") "<p>Payment Method: Cash</p>" else "<p><b>Amount Due: ${receipt.total_amount}</b></p>"}
            ${
            if (receipt.status == "pending") "<p>Printed on: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss"))}</p>" else ""
        }
            ${
            if (receipt.status == "paid") {
                """
                    <br>
                    <div class="kra">
                        <p>Printed on: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss"))}</p>
                        <p>SCU ID: ${receipt.scu_id}</p>
                        <p>SCU No: ${receipt.scu_no}</p>
                    </div>
                    """.trimIndent()
            } else ""
        }
            ${if (receipt.status == "paid") "<img src=\"file:///$qrCode\" alt=\"QR Code\">" else ""}
            <div class="footer">
                <p>Kloud Sales POS</p>
                <p>info@ubuniworks.com</p>
                <p>+254716266205</p>
                <p>Powered by Ubuniworks Solutions</p>
            </div>
        </body>
        </html>
    """.trimIndent()
    }

    override fun convertJsonToFormattedZReportString(zReport: GetZreportRes): String {
        val reportContent = zReport.body.joinToString(separator = "") { report ->
            "<p>${report.name}: ${report.value}</p>"
        }

        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <title>Receipt</title>
            <style>
                body {
                    font-family: Arial;
                    width: 400px;
                    font-size: 18px;
                    margin: 0 auto;
                    padding: 10px;
                }
                .header {
                    text-align: center;
                    font-size: 18px;
                } 
                .kra {
                     text-align: start;
                     font-size: 18px;
                 }
                .line {
                    border-top: 1px dashed #000;
                    margin-top: 5px;
                    margin-bottom: 5px;
                }
                .straight-line {
                    border-top: 1px solid #000; /* Creates a solid line */
                }
                .item-table {
                    width: 100%;
                    border-collapse: collapse;
                }
                .item-table th, .item-table td {
                    padding: 5px;
                    text-align: left;
                }
                .item-table th {
                    border-bottom: 1px solid #000;
                }
                .right-align {
                    text-align: right;
                }
            </style>
        </head>
        <body>
            <div class="header">
                <img src="https://st.pavicontech.com/api/v1/files/cinnabon-logo-1.png" width="300" height="130" />
                <p>The Mask Food and Beverages Ltd</p>
                <p>PIN No. P052237559Z</p>
                <p style="text-align: center; font-size: 20px;">P.O BOX 79702-00200<br />Nairobi, Kenya</p>
                <br />
            </div>
            <h2 style="text-align: center; font-size: 24px;">Z REPORT</h2>
   
            <br />
            $reportContent
            <div class="straight-line"></div>
     
            <p>Kloud Sales POS</p>
            <p>info@ubuniworks.com</p>
            <p>+254716266205</p>
            <p>Powered by Ubuniworks Solutions</p>
        </body>
        </html>
    """.trimIndent()
    }
}

fun main() = runBlocking {
    val results = GetReceiptsRepositoryImpl().getBills(GetReceiptsReq("pending", 1)).first { it.status == "pending" }
    val html = ReceiptRepositoryImpl().convertJsonToFormattedReceiptString(results)
    println(html)
    ReceiptRepositoryImpl().generateImage(html, "1234")
}
