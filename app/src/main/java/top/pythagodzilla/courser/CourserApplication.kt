package top.pythagodzilla.courser

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import okhttp3.OkHttpClient
import top.pythagodzilla.courser.data.dataBase.TaskDataBase
import top.pythagodzilla.courser.data.dataStore.DataStoreManager
import top.pythagodzilla.courser.network.NetworkManager
import top.pythagodzilla.courser.network.OkHttpManager
import top.pythagodzilla.courser.network.SessionCookieInterceptor
import top.pythagodzilla.courser.worker.CheckNewWorkWorker
import java.util.concurrent.TimeUnit

class CourserApplication : Application() {

    val dataStore by lazy { DataStoreManager(this) }
    val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(SessionCookieInterceptor(dataStore))
            .build()
    }
    val client: NetworkManager by lazy {
        OkHttpManager(
            client = okHttpClient,
            dataStore = dataStore
        )
    }

    val database: TaskDataBase by lazy {
        TaskDataBase.getDatabase(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        scheduleCheckTasks()
    }

    private fun createNotificationChannel() {
        // 创建通知渠道的代码
        val channel = NotificationChannel(
            "task_reminder",
            "任务提醒",
            NotificationManager.IMPORTANCE_HIGH
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun scheduleCheckTasks() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<CheckNewWorkWorker>(30, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "courser_check_tasks",
                ExistingPeriodicWorkPolicy.REPLACE, // 用KEEP会在测试状态无法加入WorkManger，有问题再改。
                request
            )
    }
}
