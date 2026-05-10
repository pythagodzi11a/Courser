package top.pythagodzilla.courser.network.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = BaseCheckLoginResponseSerializer::class)
sealed class BaseCheckLoginResponse {
    abstract val status: Int
    abstract val sessionid: String
}

@Serializable
data class SuccessCheckLoginResponse(
    val datas: SuccessCheckLoginDatas,
    override val status: Int,
    override val sessionid: String,
) : BaseCheckLoginResponse()

@Serializable
data class FailureCheckLoginResponse(
    val datas: FailureCheckLoginDatas,
    override val status: Int,
    override val sessionid: String
) : BaseCheckLoginResponse()

@Serializable
data class SuccessCheckLoginDatas(
    val columnSecurity: String,
    val totalSecurity: String,
    val userrole: Int,
    val userinfo: UserInfo,
)

@Serializable
data class FailureCheckLoginDatas(
    val errorMessage: String,
    val username: String,
    val errorCode: String
)

@Serializable
data class UserInfo(
    val photoFileId: String = "",
    val lastLoginDate: Time,
    val loginTimes: Int,
    val user: User,
    val totalOnlineTime: Int
)

@Serializable
data class User(
    val id: Int,
    val username: String,
    val realname: String,
)

object BaseCheckLoginResponseSerializer : KSerializer<BaseCheckLoginResponse> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "BaseCheckLoginResponse",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: BaseCheckLoginResponse) {
        throw NotImplementedError("Serialization not implemented for BaseCheckLoginResponse")
    }

    override fun deserialize(decoder: Decoder): BaseCheckLoginResponse {
        val input = decoder as JsonDecoder
        val jsonObj = input.decodeJsonElement().jsonObject

        return when (val statusCode = jsonObj["status"]?.jsonPrimitive?.int) {
            1 -> input.json.decodeFromJsonElement(
                SuccessCheckLoginResponse.serializer(), jsonObj
            )

            2 -> input.json.decodeFromJsonElement(
                FailureCheckLoginResponse.serializer(), jsonObj
            )

            else -> throw IllegalStateException("Unknown status code: $statusCode")
        }

    }
}