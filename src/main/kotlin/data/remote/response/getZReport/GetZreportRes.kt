package data.remote.response.getZReport

import kotlinx.serialization.Serializable

@Serializable
data class GetZreportRes(
    val report: List<Body>,
    val reprint: String
)