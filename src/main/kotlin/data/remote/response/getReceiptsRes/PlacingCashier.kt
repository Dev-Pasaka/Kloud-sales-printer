package data.remote.response.getReceiptsRes

import kotlinx.serialization.Serializable

@Serializable
data class PlacingCashier(
    val id: Int,
    val name: String
)