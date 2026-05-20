package top.pythagodzilla.courser.data.dataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDetailDao {
    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertTaskDetail(entity: TaskDetailEntity)

    @Query("SELECT * FROM task_detail WHERE taskId = :taskId AND courseId = :courseId ORDER BY savedAt DESC LIMIT 1")
    suspend fun getTaskDetailByTaskId(taskId: String, courseId: String): TaskDetailEntity?
}