package domain.usecase

import data.local.entries.Receipt
import data.repository.ReceiptDBRepositoryImpl
import data.repository.ReceiptRepositoryImpl
import domain.repository.ReceiptDBRepository
import java.io.File
import javax.imageio.ImageIO

class DeleteReceiptUseCase(
    private val db:ReceiptDBRepository = ReceiptDBRepositoryImpl()
) {
    suspend fun deleteReceipt(receiptFileName:String, id:String):Boolean = try {
        val imagePath = ReceiptRepositoryImpl().getReceiptsFolderPath()
        val filePath = "$imagePath/receipts-with-qr/$receiptFileName"
        println("Deleting file: $filePath")
        val file = File(filePath)
        val result = file.delete()

        if (result){
            db.delete(id)
        }else{
            false
        }


    }catch (e:Exception){
        e.printStackTrace()
        false
    }

}