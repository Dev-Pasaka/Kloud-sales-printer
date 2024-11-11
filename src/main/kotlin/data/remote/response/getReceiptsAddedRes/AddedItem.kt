package data.remote.response.getReceiptsAddedRes

import kotlinx.serialization.Serializable

@Serializable
data class AddedItem(
    val name: String,
    val qty: String
)