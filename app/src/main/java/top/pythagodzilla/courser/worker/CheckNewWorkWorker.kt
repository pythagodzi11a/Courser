package top.pythagodzilla.courser.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import top.pythagodzilla.courser.CourserApplication
import top.pythagodzilla.courser.R
import top.pythagodzilla.courser.data.dataBase.TasksEntities
import top.pythagodzilla.courser.network.json
import top.pythagodzilla.courser.network.response.TasksApiResponseClass

class CheckNewWorkWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val client = (applicationContext as CourserApplication).client
        val dataStore = (applicationContext as CourserApplication).dataStore
        val database = (applicationContext as CourserApplication).database
        val taskDao = database.TasksDao()

        val oldData = taskDao.getAllTasks().first()
        val freshData = client.getUndoTasksString()


        freshData.onSuccess { responseString ->
            if (oldData != null) {
                val responseObj = json.decodeFromString<TasksApiResponseClass>(responseString)
                val formerResponse = json.decodeFromString<TasksApiResponseClass>(oldData.tasksList)

                if (responseObj.datas != formerResponse.datas) {
                    sendNotification(applicationContext)
                }
            }
            taskDao.insertTasks(TasksEntities(tasksList = responseString))
        }
        return Result.success()
    }

    private fun sendNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, "task_reminder")
            .setSmallIcon(R.drawable.ic_account_box)
            .setContentTitle("新任务")
            .setContentText("有新的任务")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(1, builder.build())
        }
    }
}