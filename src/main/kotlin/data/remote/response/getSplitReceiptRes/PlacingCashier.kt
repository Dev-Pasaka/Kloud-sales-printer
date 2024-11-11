package data.remote.response.getSplitReceiptRes

import kotlinx.serialization.Serializable

@Serializable
data class PlacingCashier(
    val id: Int,
    val name: String
)