package top.pythagodzilla.courser.network.response

import kotlinx.serialization.Serializable

@Serializable
data class GetSessionResponse(
    val message: String,
    val status: Int,
    val sessionid: String,
)