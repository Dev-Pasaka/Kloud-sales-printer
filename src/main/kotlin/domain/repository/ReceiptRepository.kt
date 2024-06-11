package domain.repository

import kotlinx.coroutines.flow.Flow
import java.awt.image.BufferedImage
import java.io.File

interface ReceiptRepository {
    fun generateQRCodeImage(link: String, size: Int): BufferedImage
    fun generateReceiptWithQR(receiptContent: String, qrLink: String, filePath: String):Boolean
    suspend fun printPNGImage(filePath: String) : Flow<Pair<String, Boolean?>>

    fun generateFileName(fileName:String):String

    suspend fun watchForNewFiles(directoryPath:String): Flow<File>
}