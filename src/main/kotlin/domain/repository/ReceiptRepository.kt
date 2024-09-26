package domain.repository

import data.dto.response.ReceiptRes
import data.remote.request.GetReceiptsReq
import data.remote.response.getReceiptsRes.GetReceiptsResItem
import data.repository.GetReceiptsRepositoryImpl
import data.repository.ReceiptRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import java.awt.image.BufferedImage
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface ReceiptRepository {
    fun generateReceiptWithQR(receiptContent: String, qrLink: String,receiptId:String):Boolean
    suspend fun printPNGImage(filePath: String) : Pair<String, Boolean?>
    fun convertJsonToFormattedReceiptString(receipt: GetReceiptsResItem): String

}


suspend fun main() {
    val result = GetReceiptsRepositoryImpl().getReceipts(body = GetReceiptsReq("paid", 1))
    val formattedReceipt = ReceiptRepositoryImpl().convertJsonToFormattedReceiptString(result.first())
    println(formattedReceipt)
}

val jsonString = """
    {
        "id": 686,
        "payment_cashier_id": 5,
        "payment_station_id": 2,
        "placing_station_id": 2,
        "placing_waiter_id": 1,
        "placing_cashier_id": 3,
        "token": "b311457c-f00f-422b-8666-c48fe2e80798_20230623_124510",
        "type": "Order Out",
        "amount": "0",
        "total_amount": "300",
        "placing_device": "K-touch Smart-TILL",
        "payment_device": null,
        "note": "none",
        "customer": "none",
        "print": "0",
        "deletion_user_id": null,
        "deletion_id": null,
        "status": "paid",
        "restaurant": "pending",
        "butchery": "pending",
        "created_at": "2023-06-23 09:45:20",
        "updated_at": "2023-12-19T11:26:57.000000Z",
        "deleted_at": null,
        "placing_cashier": null,
        "placing_station": {
            "id": 2,
            "name": "Test Station"
        },
        "placing_waiter": {
            "id": 1,
            "name": "Shadrack Kristian"
        },
        "payment_cashier": {
            "id": 5,
            "name": "Mercy Chimasia"
        },
        "items": [
            {
                "id": 5,
                "transaction_id": 6,
                "product_id": 2,
                "menu_id": 2,
                "qty": 1,
                "quantity": "50 pcs",
                "price": "50",
                "name": "Honey",
                "deletion_user_id": null,
                "deletion_id": null,
                "created_at": "2023-06-23T09:45:20.000000Z",
                "updated_at": "2023-06-23T09:45:20.000000Z",
                "deleted_at": null
            },
            {
                "id": 6,
                "transaction_id": 7,
                "product_id": 3,
                "menu_id": 3,
                "qty": 2,
                "quantity": "1 bottle",
                "price": "125",
                "name": "Wine",
                "deletion_user_id": null,
                "deletion_id": null,
                "created_at": "2023-06-23T09:45:20.000000Z",
                "updated_at": "2023-06-23T09:45:20.000000Z",
                "deleted_at": null
            }
        ]
    }
    """.trimIndent()
