package data.remote.event

import data.local.entries.Receipt
import data.remote.response.getLowStockRes.GetLowStockRes
import data.remote.response.getReceiptsAddedRes.GetReceiptsAddedItemRes
import data.remote.response.getReceiptsRes.GetReceiptsResItem
import data.remote.response.getSplitReceiptRes.GetSplitReceiptRes
import data.remote.response.getZReport.GetZreportRes
import data.repository.ReceiptDBRepositoryImpl
import data.repository.ReceiptRepositoryImpl
import domain.model.PrintingStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class EventWrapper(
    val event: String,
    val data: JsonElement // Raw JSON data to deserialize dynamically
)

fun parseEvent(json: String ):Event {
    val jsonParser = Json { ignoreUnknownKeys = true }

    // Deserialize the wrapper
    val wrapper = jsonParser.decodeFromString<EventWrapper>(json)

    // Deserialize the data field based on the event type
    when (wrapper.event) {
        "bill" -> {
            val billData = jsonParser.decodeFromJsonElement<GetReceiptsResItem>(wrapper.data)
            return Event.Bill(billData)
        }
        "bill_reprint" -> {
            val billData = jsonParser.decodeFromJsonElement<GetReceiptsResItem>(wrapper.data)
            return Event.BillReprint(billData)
        }
        "bill_added" -> {
            val billData = jsonParser.decodeFromJsonElement<GetReceiptsAddedItemRes>(wrapper.data)
            return Event.BillAdded(billData)
        }
        "bill_update" -> {
            val billData = jsonParser.decodeFromJsonElement<GetReceiptsResItem>(wrapper.data)
            return Event.BillUpdated(billData)
        }
        "receipt" -> {
            val receiptData = jsonParser.decodeFromJsonElement<GetReceiptsResItem>(wrapper.data)
            return Event.Receipt(receiptData)
        }
        "receipt_reprint" -> {
            val receiptData = jsonParser.decodeFromJsonElement<GetReceiptsResItem>(wrapper.data)
            return Event.ReceiptReprint(receiptData)
        }
        "receipt_update" -> {
            val receiptData = jsonParser.decodeFromJsonElement<GetReceiptsResItem>(wrapper.data)
            return Event.ReceiptUpdated(receiptData)
        }

        "zreport" -> {
            val receiptData = jsonParser.decodeFromJsonElement<GetZreportRes>(wrapper.data)
            return Event.ZReport(receiptData)
        }
        "lowstock" -> {
            val receiptData = jsonParser.decodeFromJsonElement<GetLowStockRes>(wrapper.data)
            return Event.LowStock(receiptData)
        }

        "receipt_split" -> {
            val receiptData = jsonParser.decodeFromJsonElement<GetSplitReceiptRes>(wrapper.data)
            return Event.SplitReceipt(receiptData)
        }

        else -> {
            println("Unknown event type: ${wrapper.event}")
            return Event.Error("Unknown event type: ${wrapper.event}")

        }
    }

}

sealed class Event{
    data class Receipt(val receipt: GetReceiptsResItem):Event()
    data class ReceiptUpdated(val receipt: GetReceiptsResItem):Event()
    data class SplitReceipt(val receipt: GetSplitReceiptRes):Event()
    data class Bill(val bill: GetReceiptsResItem):Event()
    data class BillReprint(val bill: GetReceiptsResItem):Event()
    data class ReceiptReprint(val bill: GetReceiptsResItem):Event()
    data class BillUpdated(val bill: GetReceiptsResItem):Event()
    data class BillAdded(val bill: GetReceiptsAddedItemRes):Event()
    data class ZReport(val zReport: GetZreportRes):Event()
    data class LowStock(val zReport: GetLowStockRes):Event()
    data class Error(val message: String):Event()
}

