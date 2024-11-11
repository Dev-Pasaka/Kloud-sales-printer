package data.remote.response.getLowStockRes

import kotlinx.serialization.Serializable

@Serializable
data class GetLowStockRes(
    val lowstock: List<Lowstock>,
    val transaction: Transaction
)