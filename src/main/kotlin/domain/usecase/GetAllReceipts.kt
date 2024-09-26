package domain.usecase

import common.Resource
import data.repository.ReceiptDBRepositoryImpl
import data.repository.ReceiptRepositoryImpl
import domain.model.PrintingLogs
import domain.repository.ReceiptDBRepository
import domain.repository.ReceiptRepository
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllReceipts(
    private val receiptRepository: ReceiptDBRepository = ReceiptDBRepositoryImpl()
) {
    suspend fun getAllReceipts(): Flow<Resource<List<PrintingLogs>>> = flow {
        try{
            emit(Resource.Loading(message = "Loading ..."))
            val receipts = receiptRepository.getAllReceipts()
            emit(Resource.Success(data = receipts, message = "Success"))
        }
        catch (e:Exception){
            emit(Resource.Error("An expected error occurred"))
        }
    }

}