package top.pythagodzilla.courser

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import okhttp3.OkHttpClient
import top.pythagodzilla.courser.data.dataBase.TaskDataBase
import top.pythagodzilla.courser.data.dataStore.DataStoreManager
import top.pythagodzilla.courser.network.NetworkManager
import top.pythagodzilla.courser.network.OkHttpManager
import top.pythagodzilla.courser.network.SessionCookieInterceptor

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
}
