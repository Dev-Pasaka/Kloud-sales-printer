package data.repository

import data.remote.request.GetReceiptsReq
import data.remote.response.getReceiptsRes.GetReceiptsResItem
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
    private val api:KtorClient = KtorClient,
    private val keys:KeyValueStorage = KeyValueStorage
):GetReceiptsRepository {
    override // Function to get receipts
    suspend fun getReceipts(body: GetReceiptsReq): List<GetReceiptsResItem> = withContext(Dispatchers.IO) {
        val getReceipts: String = KtorClient.client.post("https://cinnabon.ubuniworks.com/api/v1/transactions") {
            val stationId = KeyValueStorage.get(key = "stationId")
            contentType(ContentType.Application.Json)
            setBody(
                """
                    {
                        "type": "${body.copy(type = "paid").type}",
                        "station": $stationId
                    }
                """.trimIndent()
            )  // This will automatically serialize the body using kotlinx.serialization
        }.bodyAsText() // This automatically deserializes the response into the expected type

        println(getReceipts)
        return@withContext Json.decodeFromString(getReceipts)
    }

    override suspend fun getBills(body: GetReceiptsReq): List<GetReceiptsResItem> = withContext(Dispatchers.IO) {
        val getReceipts: String = KtorClient.client.post("https://cinnabon.ubuniworks.com/api/v1/transactions") {
            val stationId = KeyValueStorage.get(key = "stationId")
            contentType(ContentType.Application.Json)
            setBody(
                """
                    {
                        "type": "${body.copy(type = "pending").type}",
                        "station": $stationId
                    }
                """.trimIndent()
            )  // This will automatically serialize the body using kotlinx.serialization
        }.bodyAsText() // This automatically deserializes the response into the expected type

        println(getReceipts)
        return@withContext Json.decodeFromString(getReceipts)
    }
}

suspend fun main(){
    val receipts = GetReceiptsRepositoryImpl().getBills(
        body = GetReceiptsReq(
            type = "paid",
            station = 1
        )
    ).first()

    val formattedReceipt = ReceiptRepositoryImpl().convertJsonToFormattedReceiptString(receipts)
    println(formattedReceipt)


}