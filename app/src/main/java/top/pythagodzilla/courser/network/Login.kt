package top.pythagodzilla.courser.network

import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import top.pythagodzilla.courser.data.DataStoreManager
import top.pythagodzilla.courser.network.exception.HttpException
import top.pythagodzilla.courser.network.exception.SessionExpiredException
import top.pythagodzilla.courser.network.exception.StringException
import top.pythagodzilla.courser.network.exception.UnknownException
import top.pythagodzilla.courser.network.response.BaseCheckLoginResponse
import top.pythagodzilla.courser.network.response.FailureCheckLoginResponse
import top.pythagodzilla.courser.network.response.GetSessionResponse
import top.pythagodzilla.courser.network.response.SuccessCheckLoginResponse
import top.pythagodzilla.courser.network.response.checkResponseNotLogin

class LoginModule(
    private val client: OkHttpClient = OkHttpClient(),
    private val dataStore: DataStoreManager
) {

    /**
     * 这个方法是用于登录界面登录的主要方法，同时会自动把session存入DataStore.
     */
    suspend fun commonLogin(
        deviceUuid: String,
        appVersion: String,
        password: String,
        devicePlatform: String,
        deviceVersion: String,
        username: String,
        deviceName: String
    ): Result<String> {

        Log.d("LoginModule", "Starting commonLogin with username: $username")

        val getSessionSession = getSessionId(
            deviceUuid,
            appVersion,
            password,
            devicePlatform,
            deviceVersion,
            username,
            deviceName
        )
        if (getSessionSession.status == 0){
            return Result.failure(StringException("Failed to get session ID: ${getSessionSession.message}"))
        }

        dataStore.saveSessionId(getSessionSession.sessionid)

        val loginCheckRes = loginCheck(
            deviceUuid,
            appVersion,
            password,
            devicePlatform,
            deviceVersion,
            username,
            deviceName
        )

        loginCheckRes
            .onSuccess { response ->
                when (response) {
                    is SuccessCheckLoginResponse -> {
                        if (response.sessionid.isNotEmpty()) {
                            Log.d(
                                "LoginModule",
                                "Login successful, session: ${response.sessionid}"
                            )
                            dataStore.saveSessionId(response.sessionid)

                            return Result.success(response.sessionid)
                        }
                        Log.d("LoginModule", "Login successful, but session is empty")
                    }

                    is FailureCheckLoginResponse -> {
                        Log.d("LoginModule", "$response")
                    }
                }
            }
            .onFailure { exception ->
                return Result.failure(StringException("Login failed: ${exception.message}"))
            }

        return Result.failure(StringException("Login failed"))
    }

    /**
     * 这个方法访问的是getSessionId接口，能获取到sessionId，返回的body有status，message和sessionid。
     * 其中，getSessionId接口似乎不判断任何事情，你发什么都返回一个session。
     * 所以有理由相信这个接口蛋用没有。
     * @return GetSessionResponse 对象，包含message，status和sessionid字段
     */
    suspend fun getSessionId(
        deviceUuid: String,
        appVersion: String,
        password: String,
        devicePlatform: String,
        deviceVersion: String,
        username: String,
        deviceName: String
    ): GetSessionResponse {

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
            "Request Body: deviceUuid=$deviceUuid, appVersion=$appVersion, j_password=xxxx, devicePlatform=$devicePlatform, deviceVersion=$deviceVersion, j_username=$username, deviceName=$deviceName"
        )

        val request = Request.Builder()
            .url("http://course.buct.edu.cn/mobile/getSessionId.do")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val content = response.body.string()

                return json.decodeFromString(GetSessionResponse.serializer(), content)

            }
        } catch (e: Exception) {
            Log.e(
                "OkHttpManager",
                "Error occurred during getSessionId request: ${e::class.java.name}: ${e.message}"
            )

            return GetSessionResponse(
                status = 0,
                message = "Error occurred: ${e.message}",
                sessionid = ""
            )
        }
    }

    /**
     * 这个方法访问的是loginCheck接口
     * @return Result<BaseCheckLoginResponse>，成功时返回BaseCheckLoginResponse,此处的成功指的是解析成功，即有SuccessCheckLoginResponse或者FailureCheckLoginResponse对象返回。
     * */
    suspend fun loginCheck(
        deviceUuid: String,
        appVersion: String,
        password: String,
        devicePlatform: String,
        deviceVersion: String,
        username: String,
        deviceName: String
    ): Result<BaseCheckLoginResponse> {
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

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val content = response.body.string()
                    val result = json.decodeFromString<BaseCheckLoginResponse>(content)

                    return Result.success(result)
                } else {
                    return Result.failure(
                        HttpException(
                            response.code,
                            "HTTP error: ${response.message}"
                        )
                    )
                }

            }
        } catch (e: Exception) {
            Log.e(
                "OkHttpManager",
                "Error occurred during session check request: ${e::class.java.name}: ${e.message}",
                e
            )
            return Result.failure(e)
        }
    }

    /**
     *
     */
    suspend fun isSessionValid(): Boolean {
        val request = Request.Builder()
            .url("http://course.buct.edu.cn/mobile/stuReminderList.do")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val content = response.body.string()

                val result = checkResponseNotLogin(content)
                result
                    .onSuccess {
                        Log.d("OkHttpManager", "Session is valid.")
                        return true
                    }
                    // 先做成功的吧，去他妈的鲁棒性
                    .onFailure { exception ->
                        when (exception) {
                            is HttpException -> {}
                            is SessionExpiredException -> {}
                            is UnknownException -> {}
                        }
                    }
            }
        } catch (e: Exception) {
            Log.e(
                "OkHttpManager",
                "Error occurred during session valid check request: ${e::class.java.name}: ${e.message}",
                e
            )
        }
        return false
    }


}