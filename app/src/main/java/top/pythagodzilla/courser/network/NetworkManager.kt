package top.pythagodzilla.courser.network

import android.os.Build
import android.util.Log
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import top.pythagodzilla.courser.data.dataStore.DataStoreManager
import top.pythagodzilla.courser.network.response.TasksApiResponseClass


// 创建Json解析器实例
val json = Json { ignoreUnknownKeys = true }

class NetworkManager(
    client: OkHttpClient, dataStore: DataStoreManager
) {

    // login相关处理，封装到loginModule
    private val loginModule = LoginModule(client, dataStore)
    private val getInfoModule = GetInfoModule(client)

    suspend fun commonLogin(
        appVersion: String = "8.7.1",
        password: String,
        devicePlatform: String = "android",
        deviceVersion: String = Build.VERSION.RELEASE,
        username: String,
        deviceName: String = "GUGUGAGA"
    ) = loginModule.commonLogin(
        appVersion,
        password,
        devicePlatform,
        deviceVersion,
        username,
        deviceName
    )

    private suspend fun getSessionId(
        appVersion: String,
        password: String,
        devicePlatform: String,
        deviceVersion: String,
        username: String,
        deviceName: String
    ) = loginModule.getSessionId(
        appVersion, password, devicePlatform, deviceVersion, username, deviceName
    )

    private suspend fun loginCheck(
        deviceUuid: String,
        appVersion: String,
        password: String,
        devicePlatform: String,
        deviceVersion: String,
        username: String,
        deviceName: String
    ) = loginModule.loginCheck(
        appVersion, password, devicePlatform, deviceVersion, username, deviceName
    )

    // 获取信息接口的实现，封装到GetInfoModule
    private suspend fun getUndoTasks(): Result<TasksApiResponseClass> = getInfoModule.getUndoTasks()

    suspend fun getUndoTasksString(): Result<String> = getInfoModule.getUndoTasksString()

    private suspend fun isSessionValid(sessionId: String): Boolean = loginModule.isSessionValid()

    suspend fun enterCourse(courseId: String): Result<String> = getInfoModule.enterCourse(courseId)

    suspend fun getHomeworkView(hwtid: String, context: String): Result<String> =
        getInfoModule.getHomeworkView(hwtid, context)
}

class SessionCookieInterceptor(
    private val dataStore: DataStoreManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val sessionid = runBlocking {
            dataStore.get(dataStore.session.sessionKey)
        }

        Log.d("SessionCookieInterceptor", "Read sessionid from DataStore: $sessionid")

        val newRequest = if (!sessionid.isNullOrBlank()) {
            request.newBuilder().header("Cookie", "JSESSIONID=$sessionid").build()
        } else request

        return chain.proceed(newRequest)
    }
}