package top.pythagodzilla.courser.data.dataBase

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tasks")
data class TasksEntities(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tasksList: String,
    val savedAt: LocalDateTime = LocalDateTime.now()
)