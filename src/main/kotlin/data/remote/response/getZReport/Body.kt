package data.remote.response.getZReport

import kotlinx.serialization.Serializable

@Serializable
data class Body(
    val name: String,
    val value: String
)