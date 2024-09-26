package data.remote.response.getReceiptsRes

import kotlinx.serialization.Serializable

@Serializable
data class PaymentCashier(
    val id: Int,
    val name: String
)