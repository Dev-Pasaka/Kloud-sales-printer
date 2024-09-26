package data.remote.response.getReceiptsRes

import kotlinx.serialization.Serializable

@Serializable
data class PlacingStation(
    val id: Int,
    val name: String,
    val description:String? = null
)