package data.remote.response.getReceiptsAddedRes

import kotlinx.serialization.Serializable

@Serializable
data class PlacingStation(
    val description: String,
    val id: Int,
    val name: String
)