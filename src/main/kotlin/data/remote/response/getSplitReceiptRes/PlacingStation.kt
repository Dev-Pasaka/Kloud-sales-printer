package data.remote.response.getSplitReceiptRes

import kotlinx.serialization.Serializable

@Serializable
data class PlacingStation(
    val description: String,
    val id: Int,
    val name: String
)