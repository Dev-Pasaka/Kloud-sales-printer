package data.remote.response.getReceiptsAddedRes

import kotlinx.serialization.Serializable

@Serializable
data class PlacingCashier(
    val id: Int,
    val name: String
)