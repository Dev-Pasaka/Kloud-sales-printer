package data.repository

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import data.dto.response.ReceiptRes
import data.remote.response.getReceiptsRes.GetReceiptsResItem
import de.vandermeer.asciitable.AsciiTable
import domain.repository.ReceiptRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO
import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.Copies


class ReceiptRepositoryImpl() : ReceiptRepository {
    private fun generateQRCodeImage(link: String, size: Int): BufferedImage {
        val hints = mutableMapOf<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(link, BarcodeFormat.QR_CODE, size, size, hints)

        val bufferedImage = BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bufferedImage.setRGB(x, y, if (bitMatrix[x, y]) Color.BLACK.rgb else Color.WHITE.rgb)
            }
        }
        return bufferedImage
    }

    private fun generateFileName( fileName: String): String {
        return "./receipts/$fileName"
    }

    override fun generateReceiptWithQR(receiptContent: String, qrLink: String, receiptId: String): Boolean {
        val width = 500
        var height = 0 // Increased height to accommodate the QR code at the bottom
        val qrCodeSize = 200
        val margin = 20
        val lineHeight = 20

        // Split receipt content into lines
        val textLines = receiptContent.split("\n")
        // Calculate dynamic height: text height + QR code height + margins
        val textHeight = textLines.size * lineHeight + margin * 2
        height = textHeight + qrCodeSize + margin * 2

        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val graphics = bufferedImage.createGraphics()

        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, width, height)

        graphics.color = Color.BLACK
        val font = Font("Arial", Font.PLAIN, 22)
        graphics.font = font

        var y = margin
        for (line in textLines) {
            val lineParts = line.split(":")
            if (lineParts.size == 2) {
                val key = lineParts[0].trim()
                val value = lineParts[1].trim()
                if (key.startsWith("{") && key.endsWith("}")) {
                    graphics.font = Font("Arial", Font.BOLD, 25)
                    graphics.drawString(key.substring(1, key.length - 1), margin, y)
                    y += lineHeight
                } else {
                    graphics.font = Font("Arial", Font.PLAIN, 22)
                    graphics.drawString("$key: $value", margin, y)
                    y += lineHeight
                }
            } else {
                graphics.drawString(line, margin, y)
                y += lineHeight
            }
        }

        // Generate and write QR code at the bottom
        val qrCode = generateQRCodeImage(qrLink, qrCodeSize)
        graphics.drawImage(qrCode, (width - qrCodeSize) / 2, height - qrCodeSize - margin, null)

        val filePath = generateFileName( "${receiptId}_receipt.png")
        val outputFile = File(filePath)
        return try {
            outputFile.parentFile.mkdirs()
            ImageIO.write(bufferedImage, "png", outputFile)
            graphics.dispose()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    override suspend fun printPNGImage(filePath: String): Pair<String, Boolean?> = withContext(Dispatchers.IO) {
        val file = File(filePath)
        if (!file.exists()) {
            return@withContext (Pair("File not found: $filePath", false))

        }

        val printRequestAttributeSet = HashPrintRequestAttributeSet().apply {
            add(Copies(1))
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


    override fun convertJsonToFormattedReceiptString(receipt: GetReceiptsResItem): String {
        val sb = StringBuilder()

        // Adding the header for the company
        sb.appendLine("Ubuniworks Ltd")
        sb.appendLine("P.O BOX 16779 00100")
        sb.appendLine("Nairobi, Kenya")
        sb.appendLine("".padEnd(80, '-')) // Separator

        // Determine receipt type
        val receiptType = if (receipt.status == "pending") "BILL" else "RECEIPT"
        sb.appendLine(receiptType.padEnd(50, ' ')) // Receipt Type Header
        sb.appendLine("".padEnd(80, '-')) // Separator


        sb.appendLine("".padEnd(80, ' ')) // Separator
        if (receipt.status == "pending")
            sb.appendLine("BILL No: ${receipt.id}".padEnd(50, ' '))
        else sb.appendLine("Receipt No: ${receipt.id}".padEnd(50, ' '))
        sb.appendLine("Station       : ${receipt.placing_station.name}".padEnd(80, ' '))
        sb.appendLine("Order Type    : ${receipt.type}".padEnd(80, ' '))
        sb.appendLine("Waiter        : ${receipt.placing_waiter?.name ?: "N/A"}".padEnd(80, ' '))
        sb.appendLine("Customer/Table : ".padEnd(80, ' ')) // Separator
        sb.appendLine("".padEnd(80, '-')) // Separator

        // Calculate maximum item name length
        val maxItemNameLength = maxOf(11, receipt.items.maxOfOrNull { it.name.length } ?: 0) // Ensure at least 15 padding for item name

        // Add products header with dynamic padding
        sb.appendLine(
            "Item".padEnd(maxItemNameLength) + " | " +
                    "Qty".padEnd(4) + " | " +
                    "Price".padEnd(15) + " | " +
                    "Total".padEnd(15)
        )
        sb.appendLine("".padEnd(80, '-')) // Separator

        // Add items to the table
        if (receipt.items.isEmpty()) {
            sb.appendLine("No items found in the receipt.")
        } else {
            receipt.items.forEachIndexed { index, item ->
                // Handle line breaks for long item names
                val position = index+1
                val itemNameLines = "$position. ${item.name}".chunked(10) // Split item name into chunks of 10 characters
                val itemQuantity = "${item.qty}x".padStart(4) // Quantity formatting
                val itemPrice = item.price.toDouble().toString().padEnd(15) // Price formatting
                val itemTotal = (item.price.toDouble() * item.qty.toDouble()).toString().padEnd(15) // Total formatting

                // Print each line of the item name
                for ((index, line) in itemNameLines.withIndex()) {
                    if (index == 0) {
                        sb.appendLine("${line.padEnd(maxItemNameLength)}  $itemQuantity  $itemPrice  $itemTotal")
                    } else {
                        // For subsequent lines, adjust the alignment
                        sb.appendLine("${line.padEnd(maxItemNameLength)}  ".padEnd(maxItemNameLength + 4 + 15 + 15))
                    }
                }
                sb.appendLine("".padEnd(80, )) // Separator

            }
        }
        sb.appendLine("".padEnd(80, '-')) // Separator

        // Summary rows
        val totalAmount = receipt.total_amount.toDouble()
        val amountPaid = totalAmount // Adjust this as necessary
        val balance = amountPaid - totalAmount

        // Adjust the padding for summary based on status
        if (receipt.status != "pending") {
            sb.appendLine("Total Amount:".padEnd(maxItemNameLength + 10) + " KES %.2f".format(totalAmount).padEnd(15))
            sb.appendLine("Amount Paid:".padEnd(maxItemNameLength + 10) + " KES %.2f".format(amountPaid).padEnd(15))
            sb.appendLine("Balance:".padEnd(maxItemNameLength + 10) + " KES %.2f".format(balance).padEnd(15))
        } else {
            sb.appendLine("Amount Due:".padEnd(maxItemNameLength + 10) + " KES %.2f".format(totalAmount).padEnd(15))
        }

        // Footer based on status
        if (receipt.status != "pending") {
            sb.appendLine("".padEnd(80, '-')) // Separator
            sb.appendLine("Payment Method: Cash")
            sb.appendLine("".padEnd(80, '-')) // Separator
        } else {
            sb.appendLine("".padEnd(80, '-')) // Separator
            sb.appendLine("Mpesa Till Number: 123456")
            sb.appendLine("".padEnd(80, '-')) // Separator
        }




        // Print date and customer info
        val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss"))
        sb.appendLine("Printed on: $currentTime")
        sb.appendLine("".padEnd(80, '-')) // Separator

        if (receipt.status != "pending") {
            // Customer Information
            sb.appendLine("Customer pin: ${receipt.customerpin ?: ""}")
            sb.appendLine("Customer Name: ${receipt.customername ?: ""}")
            sb.appendLine("Internal Data: ${receipt.intrlData ?: ""}")
            sb.appendLine("Receipt Sign: ${receipt.rcptSign ?: ""}")
            sb.appendLine("Etims url: ${receipt.qrurl ?: ""}")
            sb.appendLine("".padEnd(80, '-')) // Separator
        }

        // Footer section
        sb.appendLine("Powered by Ubuniworks")
        sb.appendLine("KloudSales - POS")
        sb.appendLine("Email: info@ubuniworks.com")
        sb.appendLine("Phone: +254 717 722 324")
        sb.appendLine("".padEnd(80, '-')) // Separator

        // Return the formatted receipt as a string
        return sb.toString()
    }
}
