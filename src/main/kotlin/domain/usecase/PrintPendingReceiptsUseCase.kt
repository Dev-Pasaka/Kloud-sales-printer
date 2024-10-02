package domain.usecase

import data.repository.GetReceiptsRepositoryImpl
import data.repository.ReceiptDBRepositoryImpl
import data.repository.ReceiptRepositoryImpl
import domain.model.PrintingStatus
import domain.repository.GetReceiptsRepository
import domain.repository.ReceiptDBRepository
import io.realm.kotlin.internal.platform.runBlocking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrintPendingReceiptsUseCase(
    private val receiptDBRepository: ReceiptDBRepository = ReceiptDBRepositoryImpl(),
    private val printReceiptUseCase: PrintReceiptUseCase = PrintReceiptUseCase(),
) {
    suspend fun printPendingReceipts() = withContext(Dispatchers.IO) {
        val receipts = receiptDBRepository.getAllReceipts()
        receipts.forEach { receipt ->
            val path = "${ReceiptRepositoryImpl().getReceiptsFolderPath()}/receipts-with-qr/${receipt.id}_receipt.png"
            println("Receipt location: $path")
            if (
                receipt.status?.name == PrintingStatus.PENDING.name
                ) {
                println("Printing receipt: ${receipt.status}")
                val result = printReceiptUseCase.printReceipt(path)
                if (result.second == true) {
                    receiptDBRepository.updatePrintingStatus(
                        receiptId = receipt.id,
                        printingStatus = PrintingStatus.SUCCESS
                    )
                    receiptDBRepository.delete(receipt.id)
                    println("Printing successful")
                } else {
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

suspend fun main() = runBlocking {
    while(true){
        launch {
            ContinousPullOfBIllAndReceiptsUseCase().execute()
            delay(10000)
        }
        PrintPendingReceiptsUseCase().printPendingReceipts()
        delay(10000)
    }

}