package data.remote.response.getReceiptsRes

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val created_at: String,
    val deleted_at: String? = null,
    val deletion_id: String? = null,
    val deletion_user_id: String? = null,
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