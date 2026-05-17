package top.pythagodzilla.courser.network.response

import kotlinx.serialization.Serializable

@Serializable
data class EnterCourseResponse(
    val datas: EnterCourseDatasClass,
    val status: Int,
    val sessionid: String
)

@Serializable
data class EnterCourseDatasClass(
    val stuApplyAudit: Boolean,
    val role: Int,
    val courseName: String
)