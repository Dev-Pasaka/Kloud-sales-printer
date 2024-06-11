package data.repository

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import domain.repository.ReceiptRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import javax.imageio.ImageIO
import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.Copies

class ReceiptRepositoryImpl(): ReceiptRepository {
    override fun generateQRCodeImage(link: String, size: Int): BufferedImage {
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

    override fun generateReceiptWithQR(receiptContent: String, qrLink: String, filePath: String):Boolean {
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
        return  try{
            outputFile.parentFile.mkdirs()
            ImageIO.write(bufferedImage, "png", outputFile)
            graphics.dispose()
            true
        }catch (e:Exception){
            e.printStackTrace()
            false
        }

    }

    override suspend fun printPNGImage(filePath: String): Flow<Pair<String, Boolean?>>  = flow{
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

    override fun generateFileName(fileName: String): String {
        val timestamp = System.currentTimeMillis()
        return "./receipts/${timestamp}_$fileName"
    }
    override suspend fun watchForNewFiles(directoryPath:String): Flow<File> = flow {
        val path = Paths.get(directoryPath)
        val watchService = FileSystems.getDefault().newWatchService()
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE)

        while (true) {
            val key = watchService.take()
            key.pollEvents().forEach { event ->
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    val filePath = path.resolve(event.context() as Path).toFile()
                    emit(filePath)
                }
            }
            key.reset()
        }
    }.flowOn(Dispatchers.IO)
}