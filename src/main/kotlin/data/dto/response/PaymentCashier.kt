package data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PaymentCashier(
    val id: Int,
    val name: String
)