package data.remote.response.getReceiptsRes

import kotlinx.serialization.Serializable

@Serializable
data class UpdatedPrintedReceiptsRes(
    val status:String,
    val message:String
)
