package data.remote.request

import kotlinx.serialization.Serializable


@Serializable
data class GetReceiptsReq(
    val type: String = "pending",
    val station: Int = 1
)


