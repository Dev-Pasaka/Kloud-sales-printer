import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.awt.print.Printable
import java.awt.print.PrinterJob
import java.io.File
import javax.imageio.ImageIO
import javax.print.attribute.HashPrintRequestAttributeSet

fun main() {
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

    val qrLink = "https://example.com" // Replace this with your link
    val receiptPath = "receipt_with_qr.png"

    generateReceiptWithQR(formattedContent, qrLink, receiptPath)
}


fun generateReceiptWithQR(receiptContent: String, qrLink: String, filePath: String) {
    val width = 500
    val height = 800 // Increased height to accommodate the QR code at the bottom
    val qrCodeSize = 150
    val margin = 20
    val lineHeight = 20

    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics = bufferedImage.createGraphics()

    graphics.color = Color.WHITE
    graphics.fillRect(0, 0, width, height)

    graphics.color = Color.BLACK
    val font = Font("Arial", Font.PLAIN, 20)
    graphics.font = font

    val textLines = receiptContent.split("\n")
    var y = margin
    for (line in textLines) {
        val lineParts = line.split(":")
        if (lineParts.size == 2) {
            val key = lineParts[0].trim()
            val value = lineParts[1].trim()
            if (key.startsWith("{") && key.endsWith("}")) {
                graphics.font = Font("Arial", Font.BOLD, 22)
                graphics.drawString(key.substring(1, key.length - 1), margin, y)
                y += lineHeight
            } else {
                graphics.font = Font("Arial", Font.PLAIN, 20)
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

    val outputFile = File(filePath)
    ImageIO.write(bufferedImage, "png", outputFile)

    graphics.dispose()
}


fun generateQRCodeImage(link: String, size: Int): BufferedImage {
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


