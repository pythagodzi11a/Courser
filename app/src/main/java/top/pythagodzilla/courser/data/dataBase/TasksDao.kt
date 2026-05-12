package top.pythagodzilla.courser.data.dataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {
    @Insert
    suspend fun insertTasks(entity: TasksEntities)

    @Query("SELECT * FROM tasks ORDER BY savedAt DESC LIMIT 1")
    fun getAllTasks(): Flow<TasksEntities?>
}