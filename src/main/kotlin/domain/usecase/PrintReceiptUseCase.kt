package domain.usecase

import data.repository.ReceiptRepositoryImpl
import domain.repository.ReceiptRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PrintReceiptUseCase(
    private val receiptRepository: ReceiptRepository = ReceiptRepositoryImpl()
) {
    suspend fun printReceipt(filePath:String):Pair<String, Boolean?> = withContext(Dispatchers.IO){
        receiptRepository.printPNGImage(filePath)
    }
}

