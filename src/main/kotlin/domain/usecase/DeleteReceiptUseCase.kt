package domain.usecase

import data.local.entries.Receipt
import data.repository.ReceiptDBRepositoryImpl
import domain.repository.ReceiptDBRepository
import java.io.File
import javax.imageio.ImageIO

class DeleteReceiptUseCase(
    private val db:ReceiptDBRepository = ReceiptDBRepositoryImpl()
) {
    suspend fun deleteReceipt(receiptFileName:String, id:String):Boolean = try {
        val filePath = "./receipts/$receiptFileName"
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