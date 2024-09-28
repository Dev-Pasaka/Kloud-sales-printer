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
import java.util.*

class PullNewBillsUseCase(
    private val receiptsRepository: ReceiptRepository = ReceiptRepositoryImpl(),
    private val getReceiptsRepository: GetReceiptsRepository = GetReceiptsRepositoryImpl(),
    private val db: ReceiptDBRepository = ReceiptDBRepositoryImpl(),
    private val generateReceipt: GenerateReceiptUseCase = GenerateReceiptUseCase()
) {
    suspend fun pullBills(){
        val receipts = getReceiptsRepository.getBills(body = GetReceiptsReq())
        receipts.forEach { receipt ->
            val receiptId= receipt.id.toString() ?: UUID.randomUUID().toString()
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
                )
            )
            generateReceipt.generateReceipt(
                receiptContent = receiptString,
                qrData = receipt.qrurl ?: "No URL",
                receiptId = receiptId
            )
        }
    }
}