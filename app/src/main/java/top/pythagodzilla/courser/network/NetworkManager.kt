package top.pythagodzilla.courser.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import top.pythagodzilla.courser.data.DataStore

@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
interface NetworkManager {
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
    ): Result<Boolean>

    suspend fun isSessionValid(sessionId: String): Boolean
}

class SessionCookieInterceptor(
    private val dataStore: DataStore
): Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val sessionid = runBlocking {
            dataStore.readSessionId()
        }

        val  newRequest = if (sessionid.isNullOrBlank()){
            request.newBuilder()
                .addHeader("Cookie", "sessionid=$sessionid")
                .build()
        }else request

        return chain.proceed(newRequest)
    }
}