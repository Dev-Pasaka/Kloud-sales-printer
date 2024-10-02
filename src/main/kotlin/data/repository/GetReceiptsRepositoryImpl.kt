package data.repository

import data.remote.request.GetReceiptsReq
import data.remote.response.getReceiptsRes.GetReceiptsResItem
import data.remote.response.getReceiptsRes.UpdatedPrintedReceiptsRes
import domain.repository.GetReceiptsRepository
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import utils.KeyValueStorage
import utils.KtorClient

class GetReceiptsRepositoryImpl(
    private val api: KtorClient = KtorClient,
    private val keys: KeyValueStorage = KeyValueStorage
) : GetReceiptsRepository {
    override // Function to get receipts
    suspend fun getReceipts(body: GetReceiptsReq): List<GetReceiptsResItem> = withContext(Dispatchers.IO) {
        val getReceipts: String = try {
            KtorClient.client.post("https://cinnabon-vm.ubuniworks.com/api/v1/transactions") {
                val stationId = KeyValueStorage.get(key = "stationId")
                contentType(ContentType.Application.Json)
                setBody(
                    """
                    {
                        "type": "${body.copy(type = "paid").type}",
                        "station": 1
                    }
                """.trimIndent()
                )
            }.bodyAsText()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }

        println("Response receipts:$getReceipts")
        return@withContext try {
            val res = Json.decodeFromString<List<GetReceiptsResItem>>(getReceipts)
            res.filter { it.print != "1" }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getBills(body: GetReceiptsReq): List<GetReceiptsResItem> = withContext(Dispatchers.IO) {
        val getReceipts: String = try {
            KtorClient.client.post("https://cinnabon-vm.ubuniworks.com/api/v1/transactions") {
                val stationId = KeyValueStorage.get(key = "stationId")
                contentType(ContentType.Application.Json)
                setBody(
                    """
                    {
                        "type": "${body.copy(type = "pending").type}",
                        "station": 1
                    }
                """.trimIndent()
                )  // This will automatically serialize the body using kotlinx.serialization
            }.bodyAsText()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
        println("Response bills:$getReceipts")

        return@withContext try {
            val res = Json.decodeFromString<List<GetReceiptsResItem>>(getReceipts)
            res.filter { it.print != "1" }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun updatePrintedReceiptOrBill(id: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val result =
                api.client.get("https://cinnabon-vm.ubuniworks.com/api/v1/transactions/printed/$id").bodyAsText()
            val obj = Json.decodeFromString<UpdatedPrintedReceiptsRes>(result)
            obj.status.toBoolean()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }


    }
}

suspend fun main() {
    val receipts = GetReceiptsRepositoryImpl().getBills(
        body = GetReceiptsReq(
            type = "paid",
            station = 1
        )
    ).first()

    val formattedReceipt = ReceiptRepositoryImpl().convertJsonToFormattedReceiptString(receipts)
    println(formattedReceipt)


}