import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val inputString = "Your String Here jfxchm" // Replace this with your string
    val filePath = "output.png" // File path to save the QR code

    generateQRCode(inputString, filePath)
    println("QR Code generated and saved as $filePath")
}

fun generateQRCode(inputString: String, filePath: String) {
    // Set QR code parameters
    val width = 300
    val height = 300
    val fileType = "png"

    // Set QR code hints
    val hints = mutableMapOf<EncodeHintType, Any>()
    hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
    hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H // High error correction level

    // Generate QR code
    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(inputString, BarcodeFormat.QR_CODE, width, height, hints)

    // Create buffered image to render the QR code
    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bufferedImage.setRGB(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
        }
    }

    // Save QR code as image
    val outputFile = File(filePath)
    ImageIO.write(bufferedImage, fileType, outputFile)
}