package data.remote.response.getSplitReceiptRes

import kotlinx.serialization.Serializable

@Serializable
data class GetSplitReceiptRes(
    val amount: String,
    val butchery: String,
    val created_at: String,
    val curRcptNo: String?,
    val customer: String?,
    val customername: String?,
    val customerpin: String?,
    val deleted_at: String?,
    val deletion_id: String?,
    val deletion_user_id: String?,
    val etimsid: String?,
    val etimsinvoicenumber: String?,
    val etimsstatus: String,
    val id: Int,
    val intrlData: String?,
    val items: List<Item>,
    val note: String,
    val payment_cashier: PaymentCashier,
    val payment_cashier_id: Int,
    val payment_device: String?,
    val payment_station_id: Int,
    val placing_cashier: PlacingCashier,
    val placing_cashier_id: Int,
    val placing_device: String,
    val placing_station: PlacingStation,
    val placing_station_id: Int,
    val placing_waiter: String?,
    val placing_waiter_id: String?,
    val print: String,
    val qrurl: String?,
    val rcptSign: String?,
    val restaurant: String,
    val sdcDateTime: String?,
    val status: String,
    val token: String,
    val totRcptNo: String?,
    val total_amount: String,
    val type: String,
    val updated_at: String
)