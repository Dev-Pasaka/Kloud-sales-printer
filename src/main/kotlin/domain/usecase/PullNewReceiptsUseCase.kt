package domain.usecase

import data.local.entries.Receipt
import data.remote.request.GetReceiptsReq
import data.repository.GetReceiptsRepositoryImpl
import data.repository.ReceiptDBRepositoryImpl
import data.repository.ReceiptRepositoryImpl
import domain.model.PrintingStatus
import domain.repository.GetReceiptsRepository
import domain.repository.ReceiptDBRepository
import domain.repository.ReceiptRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class PullNewReceiptsUseCase(
    private val receiptsRepository: ReceiptRepository = ReceiptRepositoryImpl(),
    private val getReceiptsRepository: GetReceiptsRepository = GetReceiptsRepositoryImpl(),
    private val db:ReceiptDBRepository = ReceiptDBRepositoryImpl(),
    private val printReceiptUseCase: PrintReceiptUseCase = PrintReceiptUseCase(),


    ) {
    suspend fun pullReceipts(){
        try {
            val receipts = getReceiptsRepository.getReceipts(body = GetReceiptsReq())
            receipts.forEach { receipt ->
                CoroutineScope(Dispatchers.IO).launch {
                    getReceiptsRepository.updatePrintedReceiptOrBill(receipt.id.toString())
                }

                val receiptId = receipt.id.toString() ?: UUID.randomUUID().toString()
                val receiptString = receiptsRepository.convertJsonToFormattedReceiptString(receipt)
                db.createReceipt(
                    printingStatusObj = Receipt(
                        _id = receiptId,
                        receiptNumber = receipt.id.toString() ?: "",
                        user = receipt.payment_cashier?.name ?: "",
                        date = receipt.created_at,
                        time = receipt.created_at,
                        data = receiptString,
                        receiptName = "${receiptId}_receipt.png",
                        url = receipt.qrurl ?: "No URL",
                        status = PrintingStatus.PENDING.name
                    ),
                    receiptString = receiptString,
                    receiptId = receiptId
                )
            }
        }catch (e:Exception){
            e.printStackTrace()
        }


    }
}

suspend fun main(){
    PullNewReceiptsUseCase().pullReceipts()
}