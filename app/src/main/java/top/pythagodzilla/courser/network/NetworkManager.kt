package top.pythagodzilla.courser.network

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import top.pythagodzilla.courser.data.DataStoreManager
import top.pythagodzilla.courser.data.response.CheckLoginDatas
import top.pythagodzilla.courser.data.response.TasksApiResponseClass

interface NetworkManager {

    // 登录相关接口
    suspend fun commonLogin(
        deviceUuid: String = "923cc477a1c01902",
        appVersion: String = "8.7.1",
        password: String,
        devicePlatform: String = "android",
        deviceVersion: String = "15",
        username: String,
        deviceName: String = "GUGUGAGA"
    ): Result<String>

    suspend fun getSessionId(
        deviceUuid: String = "923cc477a1c01902",
        appVersion: String = "8.7.1",
        password: String,
        devicePlatform: String = "android",
        deviceVersion: String = "15",
        username: String,
        deviceName: String = "GUGUGAGA"
    ): Result<String>

    suspend fun checkSession(
        deviceUuid: String,
        appVersion: String,
        password: String,
        devicePlatform: String,
        deviceVersion: String,
        username: String,
        deviceName: String
    ): Result<CheckLoginDatas>

    suspend fun isSessionValid(sessionId: String): Boolean

    // 获取信息相关接口
    suspend fun getUndoTasks(): Result<TasksApiResponseClass>
}

class SessionCookieInterceptor(
    private val dataStore: DataStoreManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val sessionid = runBlocking {
            dataStore.readSessionId()
        }

        Log.d("SessionCookieInterceptor", "Read sessionid from DataStore: $sessionid")

        val newRequest = if (!sessionid.isNullOrBlank()) {
            request.newBuilder()
                .header("Cookie", "JSESSIONID=$sessionid")
                .build()
        } else request

        return chain.proceed(newRequest)
    }
}
