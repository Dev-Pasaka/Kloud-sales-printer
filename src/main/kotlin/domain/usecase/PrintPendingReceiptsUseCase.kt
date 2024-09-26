package domain.usecase

import data.repository.ReceiptDBRepositoryImpl
import domain.model.PrintingStatus
import domain.repository.ReceiptDBRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrintPendingReceiptsUseCase(
    private val receiptDBRepository: ReceiptDBRepository = ReceiptDBRepositoryImpl(),
    private val printReceiptUseCase: PrintReceiptUseCase = PrintReceiptUseCase()
) {
    suspend fun printPendingReceipts() = withContext(Dispatchers.IO){
        val receipts = receiptDBRepository.getAllReceipts()
        receipts.forEach {receipt ->
            val path = "./receipts/${receipt.id}_receipt.png"
            if (receipt.status == PrintingStatus.PENDING){
                printReceiptUseCase.printReceipt(path).also {
                    if (it.second == true){
                        receiptDBRepository.updatePrintingStatus(
                            receiptId = receipt.id,
                            printingStatus = PrintingStatus.SUCCESS
                        )
                        println("Printing successful")
                    }else{
                        receiptDBRepository.updatePrintingStatus(
                            receiptId = receipt.id,
                            printingStatus = PrintingStatus.FALIED
                        )
                        println("Printing failed")
                    }
                }
            }



        }

    }
}