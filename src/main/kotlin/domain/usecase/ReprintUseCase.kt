package domain.usecase

import data.repository.ReceiptDBRepositoryImpl
import domain.model.PrintingStatus
import domain.repository.ReceiptDBRepository

class ReprintUseCase(
    private val repository: ReceiptDBRepository = ReceiptDBRepositoryImpl(),
    private val generateReceiptUseCase: GenerateReceiptUseCase = GenerateReceiptUseCase()

) {
    suspend fun reprint(receiptId: String, printingStatus: PrintingStatus): Boolean   {


        return try {
            val getReceipt = repository.getReceiptLogById(receiptId)
            val generateReceipt = generateReceiptUseCase.generateReceipt(
                receiptContent = getReceipt?.data ?: "",
                qrData = getReceipt?.url ?: "",
                receiptId = ""
            )
            if (!generateReceipt) return false
            val updateReceipt = repository.updatePrintingStatus(receiptId, printingStatus)
            updateReceipt
        }catch (e:Exception){
            e.printStackTrace()
            false
        }
    }

}