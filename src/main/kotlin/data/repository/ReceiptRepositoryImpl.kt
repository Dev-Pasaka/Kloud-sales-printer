package data.repository

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import data.remote.request.GetReceiptsReq
import data.remote.response.getReceiptsRes.GetReceiptsResItem
import domain.repository.ReceiptRepository
import gui.ava.html.image.generator.HtmlImageGenerator
import jpos.JposException
import jpos.POSPrinter
import jpos.POSPrinterConst
import jpos.util.JposProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.rendering.PDFRenderer
import java.awt.*
import java.awt.image.BufferedImage
import java.awt.print.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO
import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.Copies
import javax.swing.JButton
import javax.swing.JFrame


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


    fun generateImage(html: String, receiptId: String) {
        // Ensure the receipts folder exists
        ensureReceiptsFolderExists()

        // Create an instance of HtmlImageGenerator
        val imageGenerator = HtmlImageGenerator()

        // Load HTML content
        imageGenerator.loadHtml(html)

        // Set the size for the image
        imageGenerator.setSize(Dimension(100, imageGenerator.size.height))

        // Save the image as PNG to the desktop's receipts folder
        val desktopReceiptsPath = Paths.get(getReceiptsFolderPath(), "$receiptId-without-qr-receipt.png").toString()
        imageGenerator.saveAsImage(desktopReceiptsPath)
    }


    override fun generateReceiptWithQR(receiptContent: String, qrLink: String, receiptId: String): Boolean {

        val receiptsFolderPath = Paths.get(getReceiptsFolderPath(), "receipts-with-qr").toString()

        // Ensure the receipts subdirectory exists
        ensureDirectoryExists(receiptsFolderPath)

        val filePath = Paths.get("${getReceiptsFolderPath()}/receipts-with-qr", "${receiptId}_receipt.png").toString()
        val file = File(filePath)

        // Check if the image file already exists
        if (!file.exists()) {
            // Save the image as PNG
            generateImage(html = receiptContent, receiptId)
            println("Image saved as: $filePath")
        } else {
            println("Image already exists: $filePath")
        }

        val imagePath1 = Paths.get(getReceiptsFolderPath(), "$receiptId-without-qr-receipt.png").toString()
        val imagePath2 = Paths.get(getReceiptsFolderPath(), "qr_code_images", "qr-code-image.png").toString()

        return if (!file.exists()) {
            println("Image saved at: $filePath")
            mergeImagesVertically(imagePath1, imagePath2, filePath)
        } else false
    }

    fun mergeImagesVertically(imagePath1: String, imagePath2: String, outputPath: String): Boolean {
        return try {
            // Load the two images
            val img1 = ImageIO.read(File(imagePath1))
            val img2 = ImageIO.read(File(imagePath2))

            // Create a new image with a width of the larger image and a height equal to the sum of both images
            val mergedWidth = maxOf(img1.width, img2.width)
            val mergedHeight = img1.height + img2.height
            val mergedImage = BufferedImage(mergedWidth, mergedHeight, BufferedImage.TYPE_INT_RGB)

            // Get the graphics context from the merged image
            val g: Graphics2D = mergedImage.createGraphics()

            // Fill the background with white
            g.color = Color.WHITE
            g.fillRect(0, 0, mergedWidth, mergedHeight)

            // Draw the first image at (0, 0)
            g.drawImage(img1, 0, 0, null)

            // Draw the second image directly below the first image
            g.drawImage(img2, 0, img1.height, null)

            // Dispose of the graphics context to release resources
            g.dispose()

            // Save the merged image to the output path
            ImageIO.write(mergedImage, "png", File(outputPath))

            // Return true if successful
            true
        } catch (e: Exception) {
            // Log the exception or handle it as needed
            e.printStackTrace()
            // Return false if an error occurs
            false
        }
    }

    override suspend fun printPNGImage(filePath: String): Pair<String, Boolean?> = withContext(Dispatchers.IO) {
        val file = File(filePath)
        if (!file.exists()) {
            return@withContext Pair("File not found: $filePath", false)
        }

        println("Printing receipt: ${file.path}")


        try {
            convertImageToPDF(
                imagePath = filePath,
                pdfPath = "${ReceiptRepositoryImpl().getReceiptsFolderPath()}output.pdf"
            )
            printPDF("${ReceiptRepositoryImpl().getReceiptsFolderPath()}output.pdf")
            return@withContext Pair("Printing success: ", true)
        } catch (e: PrintException) {
            e.printStackTrace()
            return@withContext Pair("Printing failed: ${e.message}", false)
        }
    }



    private fun convertImageToPDF(imagePath: String, pdfPath: String) {
        try {
            // Load the image
            val image = ImageIO.read(File(imagePath))

            // Create a new PDF document
            PDDocument().use { document ->
                // Get the dimensions of the PDF page
                val pageWidth = PDRectangle.A4.width
                val pageHeight = PDRectangle.A4.height

                // Load the image into PDFBox
                val pdfImage = PDImageXObject.createFromFile(imagePath, document)

                // Calculate the number of pages needed
                val totalPages = Math.ceil(pdfImage.height / pageHeight.toDouble()).toInt()

                // Loop through and create pages for each segment of the image
                for (pageIndex in 0 until totalPages) {
                    // Create a new page
                    val page = PDPage(PDRectangle.A4)
                    document.addPage(page)

                    // Create a content stream to write to the page
                    PDPageContentStream(document, page).use { contentStream ->
                        // Calculate the y position for the image segment
                        val yPosition = -(pageIndex * pageHeight.toFloat()) // this positions the image correctly on each page

                        // Calculate the height of the portion to draw
                        val heightToDraw = Math.min(pdfImage.height - (pageIndex * pageHeight.toInt()), pageHeight.toInt())

                        // Draw the image at the top-left corner without any margins
                        contentStream.drawImage(pdfImage, 0f, yPosition, pdfImage.width.toFloat(), heightToDraw.toFloat())
                    }
                }

                // Save the PDF to the specified path
                document.save(pdfPath)
            }

            println("Image converted to PDF successfully: $pdfPath")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun printPDF(pdfPath: String) {
        try {
            // Load the PDF document
            PDDocument.load(File(pdfPath)).use { document ->
                val printJob = PrinterJob.getPrinterJob()
                val pdfRenderer = PDFRenderer(document)

                // Set up the printable object
                val printable = object : Printable {
                    override fun print(graphics: Graphics, pageFormat: java.awt.print.PageFormat, pageIndex: Int): Int {
                        if (pageIndex >= document.numberOfPages) {
                            return Printable.NO_SUCH_PAGE
                        }

                        val g2d = graphics as Graphics2D

                        // Set the width for printing to 80 mm
                        val pageWidth = 80f * 72f / 25.4f // convert 80 mm to points
                        val pdfPage = document.getPage(pageIndex)
                        val pdfWidth = pdfPage.mediaBox.width
                        val pdfHeight = pdfPage.mediaBox.height

                        // Log the width and height of the PDF page
                        println("PDF Page Width: $pdfWidth, PDF Page Height: $pdfHeight")

                        // Calculate scale factor based on the 80 mm width and minimum height
                        val scaleX = pageWidth / pdfWidth
                        val minHeight = 1200f
                        val scaleY = minHeight / pdfHeight // Scale to fit minimum height

                        // Use the smaller scale to maintain the aspect ratio
                        val finalScale = minOf(scaleX, scaleY)

                        // Log the scale factors
                        println("ScaleX: $scaleX, ScaleY: $scaleY, Final Scale: $finalScale")

                        // Set the graphics to scale
                        g2d.scale(finalScale.toDouble(), finalScale.toDouble())

                        // Render the page with the PDFRenderer
                        pdfRenderer.renderPageToGraphics(pageIndex, g2d)

                        return Printable.PAGE_EXISTS
                    }
                }

                // Set up the print job
                printJob.setPrintable(printable)

                // Start the print job
                printJob.print()
                println("Printing completed successfully.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Updated function to generate the formatted receipt string
    override fun convertJsonToFormattedReceiptString(receipt: GetReceiptsResItem): String {
        val receiptType = if (receipt.status == "pending") "BILL" else "RECEIPT"

        // Define the path where the QR code image will be saved
        val qrCodeFilePath = "qr_code_image.png" // Adjust the path as necessary

        // Generate and save the QR code image
        generateQRCodeImage(receipt.qrurl ?: "No data ", 300, qrCodeFilePath) // 100x100 size for the QR code

        val itemsHtml = receipt.items.joinToString("") { item ->
            val itemTotal = item.price.toDouble() * item.qty.toDouble()
            """
        <tr>
            <td>${item.name}</td>
            <td class="right-align">${item.qty}x</td>
            <td class="right-align">${item.price}</td>
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
            """
        """.trimIndent().format(totalAmount)
        }

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
                <img src="https://st.pavicontech.com/api/v1/files/cinnabon-logo-1.png" width="300" height="130">
                <p style="text-align: center; font-size: 20px;">P.O BOX 79702-00200<br>Nairobi, Kenya</p>
            </div>
            <h2 style="text-align: center; font-size: 24px;">${receiptType.padEnd(50)}</h2>
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
                    <th>Price</th>
                    <th>Total</th>
                </tr>
                $itemsHtml
            </table>
            <div class="straight-line"></div>
            $footerHtml
            ${if (receipt.status == "paid") "<p>Payment Method: Cash  </p>" else "<p><b>Amount Due:  ${receipt.total_amount}</b></p>"}
            ${if (receipt.status == "pending") "<p>Cinnabon (k) ltd </p>" else ""}
            ${if (receipt.status == "pending") "<p>Printed on: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss"))}</p>" else ""}
            <div class="straight-line"></div>
            ${
            if (receipt.status == "paid"){
                """
                    <div class="kra">
                        <p>Printed on: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss"))}</p>
                        <p>SCU ID    : ${receipt.scu_id}</p>
                        <p>SCU No    : ${receipt.scu_no}</p>
                     </div>
                    """.trimIndent()
            }else ""
        }
        </body>
        </html>
    """.trimIndent()
    }
}



suspend fun main() {
    // Load your image here
    ReceiptRepositoryImpl().generateImage(
        html = ReceiptRepositoryImpl().convertJsonToFormattedReceiptString(
            GetReceiptsRepositoryImpl().getReceipts(GetReceiptsReq()).first()
        ),
        "1234"
    )
    val imagePath = "C:\\Users\\lenovo\\Desktop\\receipts\\receipts-with-qr\\8_receipt.png"

    // Specify the output PDF path including the filename
    val pdfPath = "C:\\Users\\lenovo\\Desktop\\receipts\\output.pdf"

    // Convert image to PDF
    convertImageToPDF(imagePath, pdfPath)

    // Print the generated PDF
    printPDF2(pdfPath)
}

fun convertImageToPDF(imagePath: String, pdfPath: String) {
    try {
        // Load the image
        val image = ImageIO.read(File(imagePath))

        // Create a new PDF document
        PDDocument().use { document ->
            // Get the dimensions of the PDF page
            val pageWidth = PDRectangle.A4.width
            val pageHeight = PDRectangle.A4.height

            // Load the image into PDFBox
            val pdfImage = PDImageXObject.createFromFile(imagePath, document)

            // Calculate the number of pages needed
            val totalPages = Math.ceil(pdfImage.height / pageHeight.toDouble()).toInt()

            // Loop through and create pages for each segment of the image
            for (pageIndex in 0 until totalPages) {
                // Create a new page
                val page = PDPage(PDRectangle.A4)
                document.addPage(page)

                // Create a content stream to write to the page
                PDPageContentStream(document, page).use { contentStream ->
                    // Calculate the y position for the image segment
                    val yPosition = -(pageIndex * pageHeight.toFloat()) // this positions the image correctly on each page

                    // Calculate the height of the portion to draw
                    val heightToDraw = Math.min(pdfImage.height - (pageIndex * pageHeight.toInt()), pageHeight.toInt())

                    // Draw the image at the top-left corner without any margins
                    contentStream.drawImage(pdfImage, 0f, yPosition, pdfImage.width.toFloat(), heightToDraw.toFloat())
                }
            }

            // Save the PDF to the specified path
            document.save(pdfPath)
        }

        println("Image converted to PDF successfully: $pdfPath")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun printPDF2(pdfPath: String) {
    try {
        // Load the PDF document
        PDDocument.load(File(pdfPath)).use { document ->
            val printJob = PrinterJob.getPrinterJob()
            val pdfRenderer = PDFRenderer(document)

            // Set up the printable object
            val printable = object : Printable {
                override fun print(graphics: Graphics, pageFormat: java.awt.print.PageFormat, pageIndex: Int): Int {
                    if (pageIndex >= document.numberOfPages) {
                        return Printable.NO_SUCH_PAGE
                    }

                    val g2d = graphics as Graphics2D

                    // Set the width for printing to 80 mm
                    val pageWidth = 80f * 72f / 25.4f // convert 80 mm to points
                    val pdfPage = document.getPage(pageIndex)
                    val pdfWidth = pdfPage.mediaBox.width
                    val pdfHeight = pdfPage.mediaBox.height

                    // Log the width and height of the PDF page
                    println("PDF Page Width: $pdfWidth, PDF Page Height: $pdfHeight")

                    // Calculate scale factor based on the 80 mm width and minimum height
                    val scaleX = pageWidth / pdfWidth
                    val minHeight = 1200f
                    val scaleY = minHeight / pdfHeight // Scale to fit minimum height

                    // Use the smaller scale to maintain the aspect ratio
                    val finalScale = minOf(scaleX, scaleY)

                    // Log the scale factors
                    println("ScaleX: $scaleX, ScaleY: $scaleY, Final Scale: $finalScale")

                    // Set the graphics to scale
                    g2d.scale(finalScale.toDouble(), finalScale.toDouble())

                    // Render the page with the PDFRenderer
                    pdfRenderer.renderPageToGraphics(pageIndex, g2d)

                    return Printable.PAGE_EXISTS
                }
            }

            // Set up the print job
            printJob.setPrintable(printable)

            // Start the print job
            printJob.print()
            println("Printing completed successfully.")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
