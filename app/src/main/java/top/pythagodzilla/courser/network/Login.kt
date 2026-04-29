package top.pythagodzilla.courser.network

import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import top.pythagodzilla.courser.data.DataStoreManager

class LoginModule(private val client: OkHttpClient = OkHttpClient(), dataStore: DataStoreManager) {
    suspend fun getSessionId(
        deviceUuid: String,
        appVersion: String,
        password: String,
        devicePlatform: String,
        deviceVersion: String,
        username: String,
        deviceName: String
    ): Result<String> {
        return try {
            val body = FormBody.Builder()
                .add("deviceUuid", deviceUuid)
                .add("appVersion", appVersion)
                .add("j_password", password)
                .add("devicePlatform", devicePlatform)
                .add("deviceVersion", deviceVersion)
                .add("j_username", username)
                .add("deviceName", deviceName)
                .build()

            Log.d(
                "OkHttpManager",
                "Request Body: deviceUuid=$deviceUuid, appVersion=$appVersion, j_password=$password, devicePlatform=$devicePlatform, deviceVersion=$deviceVersion, j_username=$username, deviceName=$deviceName"
            )
            Log.d("OkHttpManager", body.toString())

            val request = Request.Builder()
                .url("http://course.buct.edu.cn/mobile/getSessionId.do")
                .post(body)
                .build()

            val notificationRequest = Request.Builder()
                .url("http://course.buct.edu.cn/mobile/login_check.do")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                val code = response.code
                val text = response.body.string()
                Log.d("OkHttpManager", "httpCode=$code")
                Log.d("OkHttpManager", "responseBody=$text")

                if (!response.isSuccessful) {
                    return Result.failure(Exception("HTTP $code: $text"))
                }

                val json = JSONObject(text) // 如果这里炸，会在 catch 里看到
                val status = json.optInt("status", 0)
                val sessionId = json.optString("sessionid", "")
                if (status == 1 && sessionId.isNotBlank()) {
                } else {
                    Result.failure<Exception>(Exception("Login failed: $text"))
                }
            }

            client.newCall(notificationRequest).execute().use { response ->
                val text = response.body.string()
                Log.d(
                    "OkHttpManager",
                    "Notification request httpCode=${response.code}, responseBody=$text"
                )
                if (!response.isSuccessful) {
                    Log.d(
                        "OkHttpManager",
                        "Notification request failed: HTTP ${response.code}: $text"
                    )
                    return Result.failure(Exception("Notification request failed: HTTP ${response.code}: $text"))
                }

                val json = JSONObject(text)
                val status = json.optInt("status", 0)
                if (status == 1) {
                    return Result.success(json.optString("sessionid", ""))
                } else {
                    Result.failure(Exception("Notification request failed: $text"))
                }


            }


        } catch (e: Exception) {
            Log.e("OkHttpManager", "Error during login: ${e::class.java.name}: ${e.message}", e)
            Result.failure(e)
        }

    }

    suspend fun checkSession(
        deviceUuid: String,
        appVersion: String,
        password: String,
        devicePlatform: String,
        deviceVersion: String,
        username: String,
        deviceName: String
    ): Result<Boolean> {
        val body = FormBody.Builder()
            .add("deviceUuid", deviceUuid)
            .add("appVersion", appVersion)
            .add("j_password", password)
            .add("devicePlatform", devicePlatform)
            .add("deviceVersion", deviceVersion)
            .add("j_username", username)
            .add("deviceName", deviceName)
            .build()

        val request: Request = Request.Builder()
            .url("http://course.buct.edu.cn/mobile/login_check.do")
            .post(body)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result.failure(Exception("HTTP ${response.code}: ${response.body.string()}"))
                }

                val json = JSONObject(response.body.string())
                val status = json.optInt("status", 0)
                Result.success(status == 1)
            }
        } catch (e: Exception) {
            Log.e(
                "OkHttpManager",
                "Error during session check: ${e::class.java.name}: ${e.message}",
                e
            )
            Result.failure(e)
        }
    }

    suspend fun isSessionValid(): Boolean {
        val request = Request.Builder()
            .url("http://course.buct.edu.cn/mobile/stuReminderList.do")
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(
                        "OkHttpManager",
                        "Error happened when check session. response not succeed. HTTP ${response.code}: ${response.body.string()}"
                    )
                    return false
                }

                val text = response.body.string()
                val json = JSONObject(text)
                val status = json.optInt("status", -2)

                return status == 1
            }
        } catch (e: Exception) {
            Log.e(
                "OkHttpManager",
                "Error during session validation: ${e::class.java.name}: ${e.message}",
                e
            )
            return false
        }
    }
}