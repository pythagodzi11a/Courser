package top.pythagodzilla.courser

import android.app.Application
import okhttp3.OkHttpClient
import top.pythagodzilla.courser.data.DataStoreManager
import top.pythagodzilla.courser.data.dataBase.TaskDataBase
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
}
