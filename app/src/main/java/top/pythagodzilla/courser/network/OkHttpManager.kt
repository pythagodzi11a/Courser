package top.pythagodzilla.courser.network

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import top.pythagodzilla.courser.data.DataStoreManager
import top.pythagodzilla.courser.network.response.TasksApiResponseClass


// 创建Json解析器实例
val json = Json { ignoreUnknownKeys = true }

class OkHttpManager(
    client: OkHttpClient,
    dataStore: DataStoreManager
) : NetworkManager {

    // login相关处理，封装到loginModule
    private val loginModule = LoginModule(client, dataStore)
    private val getInfoModule = GetInfoModule(client)

    override suspend fun commonLogin(
        deviceUuid: String,
        appVersion: String,
        password: String,
        devicePlatform: String,
        deviceVersion: String,
        username: String,
        deviceName: String
    ) = loginModule.commonLogin(
        deviceUuid,
        appVersion,
        password,
        devicePlatform,
        deviceVersion,
        username,
        deviceName
    )

    override suspend fun getSessionId(
        deviceUuid: String,
        appVersion: String,
        password: String,
        devicePlatform: String,
        deviceVersion: String,
        username: String,
        deviceName: String
    ) = loginModule.getSessionId(
        deviceUuid,
        appVersion,
        password,
        devicePlatform,
        deviceVersion,
        username,
        deviceName
    )

    override suspend fun loginCheck(
        deviceUuid: String,
        appVersion: String,
        password: String,
        devicePlatform: String,
        deviceVersion: String,
        username: String,
        deviceName: String
    ) = loginModule.loginCheck(
        deviceUuid,
        appVersion,
        password,
        devicePlatform,
        deviceVersion,
        username,
        deviceName
    )

    // 获取信息接口的实现，封装到GetInfoModule
    override suspend fun getUndoTasks(): Result<TasksApiResponseClass> =
        getInfoModule.getUndoTasks()

    override suspend fun getUndoTasksString(): Result<String> = getInfoModule.getUndoTasksString()


    override suspend fun isSessionValid(sessionId: String): Boolean = loginModule.isSessionValid()
}