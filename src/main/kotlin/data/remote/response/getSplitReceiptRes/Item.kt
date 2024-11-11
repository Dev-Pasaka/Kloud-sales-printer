package data.remote.response.getSplitReceiptRes

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val created_at: String,
    val deleted_at: String?,
    val deletion_id: String?,
    val deletion_user_id: String?,
    val id: Int,
    val menu_id: Int,
    val name: String,
    val price: String,
    val product_id: Int,
    val qty: Int,
    val quantity: String,
    val transaction_id: Int,
    val updated_at: String
)