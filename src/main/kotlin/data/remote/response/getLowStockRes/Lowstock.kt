package data.remote.response.getLowStockRes

import kotlinx.serialization.Serializable

@Serializable
data class Lowstock(
    val amount: Int,
    val name: String,
    val qty: String
)