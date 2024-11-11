package data.remote.response.getLowStockRes

import kotlinx.serialization.Serializable

@Serializable
data class PlacingWaiter(
    val id: Int,
    val name: String
)