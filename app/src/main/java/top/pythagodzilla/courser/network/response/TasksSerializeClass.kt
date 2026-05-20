package top.pythagodzilla.courser.network.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


@Serializable
data class TasksApiResponseClass(
    val datas: List<@Serializable(with = ReminderListSerializer::class) ReminderListClass>,
    val status: Int,
    val sessionid: String
)

object ReminderListSerializer : KSerializer<ReminderListClass> {

    override val descriptor = buildClassSerialDescriptor("ReminderListClass")
    override fun serialize(encoder: Encoder, value: ReminderListClass) = Unit
    override fun deserialize(decoder: Decoder): ReminderListClass {
        val input = decoder as JsonDecoder
        val jsonObj = input.decodeJsonElement().jsonObject

        val courseId = jsonObj["courseId"]?.jsonPrimitive?.int ?: 0
        val courseName = jsonObj["courseName"]?.jsonPrimitive?.content ?: ""

        val reminderList = mutableListOf<TaskItem>()

        jsonObj.forEach { (key, value) ->
            if (key.startsWith("reminderList")) {
                value.jsonArray.forEach { item ->
                    val itemObj = item.jsonObject
                    val parsed: TaskItem =
                        if ("expiredTime" in itemObj || "examType" in itemObj) {
                            input.json.decodeFromJsonElement< ExamResponseClass>(item)

                        } else {
                            input.json.decodeFromJsonElement<HomeworkResponseClass>(item)
                        }
                    reminderList += parsed
                }
            }
        }

        return ReminderListClass(
            courseName = courseName, courseId = courseId,
            reminderList = reminderList
        )
    }
}


@Serializable
data class ReminderListClass(
    val courseName: String,
    val courseId: Int,
    val reminderList: List<TaskItem>
)

@Serializable
sealed class TaskItem {
    abstract val id: Int
    abstract val title: String
    abstract val publishStatus: Boolean
    abstract val startTime: Time
    abstract val endTime: Time
}


@Serializable
data class HomeworkResponseClass(

    override val id: Int,
    override val title: String,
    override val publishStatus: Boolean,
    @SerialName("pubTime")
    override val startTime: Time,
    @SerialName("deadline")
    override val endTime: Time,

    val revision: Boolean,
    val fullmark: Int,
    val lessId: Int,

    ) : TaskItem()

@Serializable
data class ExamResponseClass(
    override val id: Int,
    override val title: String,
    override val publishStatus: Boolean,
    @SerialName("starttime")
    override val startTime: Time,
    @SerialName("expiredTime")
    override val endTime: Time,

    val unCommentStuNum: Int,
    val commitStuNum: Int,
    val reExamTimes: Int,
    val limitedTime: Int,
    val examType: Int,
    val cateId: Int,
) : TaskItem()

