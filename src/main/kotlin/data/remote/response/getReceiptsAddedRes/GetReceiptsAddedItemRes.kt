package data.remote.response.getReceiptsAddedRes

import kotlinx.serialization.Serializable

@Serializable
data class GetReceiptsAddedItemRes(
    val added_items: List<AddedItem>,
    val transaction: Transaction
)