package data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PlacingStation(
    val id: Int,
    val name: String
)