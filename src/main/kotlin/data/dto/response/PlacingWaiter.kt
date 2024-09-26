package data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PlacingWaiter(
    val id: Int,
    val name: String
)