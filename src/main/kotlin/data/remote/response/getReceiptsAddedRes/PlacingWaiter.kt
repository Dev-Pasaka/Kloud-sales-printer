package data.remote.response.getReceiptsAddedRes

import kotlinx.serialization.Serializable

@Serializable
data class PlacingWaiter(
    val id: Int,
    val name: String
)