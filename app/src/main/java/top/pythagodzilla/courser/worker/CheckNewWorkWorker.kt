package top.pythagodzilla.courser.worker

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import top.pythagodzilla.courser.CourserApplication
import top.pythagodzilla.courser.R
import top.pythagodzilla.courser.data.dataBase.TasksDao
import top.pythagodzilla.courser.data.dataBase.TasksEntities
import top.pythagodzilla.courser.data.dataStore.DataStoreManager
import top.pythagodzilla.courser.network.NetworkManager
import top.pythagodzilla.courser.network.exception.SessionExpiredException
import top.pythagodzilla.courser.network.json
import top.pythagodzilla.courser.network.response.TasksApiResponseClass


/**
 * todo:
 * 1. 处理session过期，触发自动登录。
 * 2. 新旧对比，而不是整个对比。
 */
class CheckNewWorkWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val client = (applicationContext as CourserApplication).client
        val dataStore = (applicationContext as CourserApplication).dataStore
        val database = (applicationContext as CourserApplication).database
        val taskDao = database.TasksDao()

        if (guard(dataStore)) return Result.success()

        return fetchAndCompareWork(
            taskDao = taskDao,
            client = client,
            dataStore = dataStore,
            retryCount = 0
        )

    }

    private suspend fun fetchAndCompareWork(
        taskDao: TasksDao,
        client: NetworkManager,
        dataStore: DataStoreManager,
        retryCount: Int
    ): Result {
        val oldData = taskDao.getAllTasks().first()
        val freshData = client.getUndoTasksString()

        Log.d("CourserWorker", "Worker triggered, retryCount: $retryCount")

        freshData.onSuccess { responseString ->
            val newResponse = json.decodeFromString<TasksApiResponseClass>(responseString)

            if (oldData != null) {
                val oldResponse = json.decodeFromString<TasksApiResponseClass>(oldData.tasksList)

                val oldIds = oldResponse.datas
                    .flatMap { it.reminderList.map { task -> task.id } }
                    .toSet()
                val newIds = newResponse.datas
                    .flatMap { it.reminderList.map { task -> task.id } }
                    .toSet()
                val addedIds = newIds - oldIds

                if (addedIds.isNotEmpty()) {
                    val addedTasks = mutableListOf<Pair<String, String>>()
                    newResponse.datas.forEach { course ->
                        course.reminderList.forEach { task ->
                            if (task.id in addedIds) {
                                addedTasks += course.courseName to task.title
                            }
                        }
                    }
                    sendNotification(applicationContext, addedTasks)
                }
            }
            taskDao.insertTasks(TasksEntities(tasksList = responseString))


        }.onFailure { exception ->
            when (exception) {
                is SessionExpiredException -> {
                    if (retryCount >= 1) return Result.success()

                    val (username, password) = withContext(Dispatchers.IO) {
                        dataStore.readLoginInfo()
                    }

                    if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                        val result = client.commonLogin(
                            username = username,
                            password = password
                        )
                        result.onSuccess {
                            return fetchAndCompareWork(
                                taskDao = taskDao,
                                client = client,
                                dataStore = dataStore,
                                retryCount = retryCount + 1
                            )
                        }
                            .onFailure {
                                Log.e("CourserWorker", "自动登录失败，无法获取新任务")
                                return Result.retry()
                            }
                    }
                }
            }
        }
        return Result.success()
    }


    private fun sendNotification(context: Context,tasks: List<Pair<String, String>>) {
        val title = if (tasks.size == 1) "新任务" else "有${tasks.size}个新任务"
        val body = tasks.joinToString("\n") { (course,title) -> "《$course》 $title" }

        val builder = NotificationCompat.Builder(context, "task_reminder")
            .setSmallIcon(R.drawable.outline_notifications_24)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context)
                .notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    private suspend fun guard(dataStoreManager: DataStoreManager): Boolean {
        val firstStart = dataStoreManager.readFirstStart()
        val (username, password) = dataStoreManager.readLoginInfo()

        return (firstStart || username == null || password == null)
    }
}