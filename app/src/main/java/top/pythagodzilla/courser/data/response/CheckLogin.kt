package top.pythagodzilla.courser.data.response

import kotlinx.serialization.Serializable

@Serializable
data class CheckLoginResponse(
    val datas: CheckLoginDatas,
    val status: Int,
    val sessionid: String,
)

@Serializable
data class CheckLoginDatas(
    val columnSecurity: String,
    val totalSecurity: String,
    val userrole: Int,
    val userinfo: UserInfo,
)

@Serializable
data class UserInfo(
    val photoFileId: String,
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