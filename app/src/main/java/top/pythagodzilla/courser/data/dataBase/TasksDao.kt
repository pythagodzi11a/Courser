package top.pythagodzilla.courser.data.dataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TasksDao {
    @Insert
    fun insertTasks(entity: TasksEntities)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<TasksEntities>
}