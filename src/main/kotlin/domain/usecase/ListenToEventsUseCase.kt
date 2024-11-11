package domain.usecase

import common.Resource
import data.local.entries.Receipt
import data.remote.event.Event
import data.repository.GetReceiptsRepositoryImpl
import data.repository.ReceiptDBRepositoryImpl
import data.repository.ReceiptRepositoryImpl
import domain.model.PrintingStatus
import domain.repository.GetReceiptsRepository
import io.realm.kotlin.internal.platform.isWindows
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ListenToEventsUseCase(
    private val getReceiptsRepository: GetReceiptsRepository = GetReceiptsRepositoryImpl(),

    ) {
    suspend fun event(): Unit = withContext(Dispatchers.IO) {
        getReceiptsRepository.listenReceipts().collect{event->
            when(event){
                is Resource.Success ->{
                    when(val data = event.data){
                        is Event.Receipt ->{
                            val html = ReceiptRepositoryImpl().convertJsonToFormattedReceiptString(data.receipt)
                            ReceiptRepositoryImpl().generateImage(
                                html = html,
                                receiptId = data.receipt.id.toString()
                            )
                            ReceiptDBRepositoryImpl().createReceipt(
                                receiptObj = Receipt(
                                    _id = data.receipt.id.toString(),
                                    receiptNumber = data.receipt.id.toString(),
                                    user = data.receipt.placing_waiter?.name ?: "N/A",
                                    date = data.receipt.created_at,
                                    time = System.currentTimeMillis().toString(),
                                    data = html,
                                    receiptName = data.receipt.customer ,
                                    url = data.receipt.qrurl ?: "No data",
                                    status = PrintingStatus.PENDING.name
                                ),
                                receiptString = html,
                                receiptId = data.receipt.id.toString()
                            )
                        }
                        is Event.BillReprint ->{
                            val html = ReceiptRepositoryImpl().convertJsonToFormattedBillReprintString(data.bill)
                            ReceiptRepositoryImpl().generateImage(
                                html = html,
                                receiptId = data.bill.id.toString()
                            )
                        }
                        is Event.ReceiptReprint ->{
                            val html = ReceiptRepositoryImpl().convertJsonToFormattedReceiptReprintString(data.bill)
                            ReceiptRepositoryImpl().generateImage(
                                html = html,
                                receiptId = data.bill.id.toString()
                            )
                        }
                        is Event.Bill ->{
                            val html = ReceiptRepositoryImpl().convertJsonToFormattedBillReprintString(data.bill)
                            ReceiptRepositoryImpl().generateImage(
                                html = html,
                                receiptId = data.bill.id.toString()
                            )
                            ReceiptDBRepositoryImpl().createReceipt(
                                receiptObj = Receipt(
                                    _id = data.bill.id.toString(),
                                    receiptNumber = data.bill.id.toString(),
                                    user = data.bill.placing_waiter?.name ?: "N/A",
                                    date = data.bill.created_at,
                                    time = System.currentTimeMillis().toString(),
                                    data = html,
                                    receiptName = data.bill.customer ,
                                    url = data.bill.qrurl ?: "No data",
                                    status = PrintingStatus.PENDING.name
                                ),
                                receiptString = html,
                                receiptId = data.bill.id.toString()
                            )
                        }
                        is Event.BillAdded ->{
                            val html = ReceiptRepositoryImpl().convertJsonToFormattedReceiptAddedItemsString(data.bill)
                            ReceiptRepositoryImpl().generateImage(
                                html = html,
                                receiptId = data.bill.transaction.id.toString()
                            )
                            ReceiptDBRepositoryImpl().createReceipt(
                                receiptObj = Receipt(
                                    _id = data.bill.transaction.id.toString(),
                                    receiptNumber = data.bill.transaction.id.toString(),
                                    user = data.bill.transaction.placing_waiter.name ?: "N/A",
                                    date = data.bill.transaction.created_at,
                                    time = System.currentTimeMillis().toString(),
                                    data = html,
                                    receiptName = data.bill.transaction.customer ?: "N/A" ,
                                    url = data.bill.transaction.qrurl ?: "No data",
                                    status = PrintingStatus.PENDING.name
                                ),
                                receiptString = html,
                                receiptId = data.bill.transaction.id.toString()
                            )
                        }
                        is Event.ZReport ->{
                            val html = ReceiptRepositoryImpl().convertJsonToFormattedZReportString(data.zReport)
                            ReceiptRepositoryImpl().generateZReportImage(
                                html = html,
                            )
                        }
                        is Event.LowStock ->{
                            val html = ReceiptRepositoryImpl().convertJsonToFormattedLowStockString(data.zReport)
                            ReceiptRepositoryImpl().generateLowStockImage(
                                html = html,
                            )
                        }
                        is Event.SplitReceipt ->{
                            val html = ReceiptRepositoryImpl().convertJsonToFormattedSplitReceiptItemsString(data.receipt)
                            ReceiptRepositoryImpl().generateImage(
                                html = html,
                                receiptId = data.receipt.id.toString()
                            )
                        }
                        is Event.BillUpdated->{
                            val html = ReceiptRepositoryImpl().convertJsonToFormattedBillUpdateString(data.bill)
                            ReceiptRepositoryImpl().generateImage(
                                html = html,
                                receiptId = data.bill.id.toString()
                            )
                        }
                        is Event.ReceiptUpdated->{
                            val html = ReceiptRepositoryImpl().convertJsonToFormattedReceiptUpdateString(data.receipt)
                            ReceiptRepositoryImpl().generateImage(
                                html = html,
                                receiptId = data.receipt.id.toString()
                            )
                        }
                        is Event.Error ->{
                            println("An error occurred: ${data.message}")
                        }
                        else ->{
                            println("An registered event")

                        }
                    }

                }
                is Resource.Loading ->{

                }
                is Resource.Error ->{

                }
            }
        }
    }
}