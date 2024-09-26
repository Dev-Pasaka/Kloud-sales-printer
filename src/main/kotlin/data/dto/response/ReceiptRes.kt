package data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptRes(
    val amount: String,
    val butchery: String,
    val created_at: String,
    val customer: String,
    val deleted_at: String?,
    val deletion_id: String?,
    val deletion_user_id: String?,
    val id: Int,
    val items: List<Item>,
    val note: String,
    val payment_cashier: PaymentCashier,
    val payment_cashier_id: Int,
    val payment_device: String?,
    val payment_station_id: Int,
    val placing_cashier: String?,
    val placing_cashier_id: Int,
    val placing_device: String,
    val placing_station: PlacingStation,
    val placing_station_id: Int,
    val placing_waiter: PlacingWaiter,
    val placing_waiter_id: Int,
    val print: String,
    val restaurant: String,
    val status: String,
    val token: String,
    val total_amount: String,
    val type: String,
    val updated_at: String
)