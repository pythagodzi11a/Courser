package top.pythagodzilla.courser.data.dataBase

import androidx.room.Entity
import java.time.LocalDateTime

@Entity(tableName = "task_detail", primaryKeys = ["taskId", "courseId"])
data class TaskDetailEntity(
    val taskId: String,
    val courseId: String,
    val taskDetail: String,
    val savedAt: LocalDateTime = LocalDateTime.now()
)