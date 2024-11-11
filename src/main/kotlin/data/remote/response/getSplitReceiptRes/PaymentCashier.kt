package data.remote.response.getSplitReceiptRes

import kotlinx.serialization.Serializable

@Serializable
data class PaymentCashier(
    val id: Int,
    val name: String
)