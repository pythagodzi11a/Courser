package top.pythagodzilla.courser.network.response

import kotlinx.serialization.Serializable

@Serializable
data class HomeworkViewResponse(
    val datas: HomeworkViewDatasClass,
    val status: Int,
    val sessionid: String
)

@Serializable
data class HomeworkViewDatasClass(
    val id: Int,
    val hasSubmit: Boolean,
    val maySubmit: Boolean,
    val mayModify: Int,
    val hwTaskId: Int,
    val back: Boolean,
    val taskContent: String,
    val taskTitle: String
)