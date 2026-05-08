package top.pythagodzilla.courser.data.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@TypeConverters(Convertor::class)
@Database(entities = [TasksEntities::class], version = 1, exportSchema = false)
abstract class TaskDataBase : RoomDatabase() {
    abstract fun TasksDao(): TasksDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDataBase? = null

        fun getDatabase(context: Context): TaskDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDataBase::class.java,
                    "task_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}