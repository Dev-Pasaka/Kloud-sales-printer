package domain.usecase

import data.repository.ReceiptRepositoryImpl
import domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class WatchNewReceiptsUseCase(
    private val receiptRepository: ReceiptRepository =ReceiptRepositoryImpl()
) {
    suspend fun watchNewReceipts(directoryPath:String): Flow<File> = flow{
        receiptRepository.watchForNewFiles(directoryPath)
    }
}