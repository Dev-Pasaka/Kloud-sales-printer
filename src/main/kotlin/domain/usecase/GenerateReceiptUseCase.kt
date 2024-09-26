package domain.usecase

import data.repository.ReceiptRepositoryImpl
import domain.repository.ReceiptRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GenerateReceiptUseCase(
    private val receiptRepository: ReceiptRepository = ReceiptRepositoryImpl()
) {
    suspend fun generateReceipt(
        receiptContent:String,
        qrData:String,
        receiptId:String
    ):Boolean = withContext(Dispatchers.IO){
        receiptRepository.generateReceiptWithQR(
            receiptContent = receiptContent,
            qrLink = qrData,
            receiptId  = receiptId
        )
    }
}
