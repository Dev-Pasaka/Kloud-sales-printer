package data.remote.response.getZReport

import kotlinx.serialization.Serializable

@Serializable
data class GetZreportRes(
    val body: List<Body>,
    val printed: String,
    val status: Boolean
)