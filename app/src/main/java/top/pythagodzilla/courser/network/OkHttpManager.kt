package top.pythagodzilla.courser.network

import okhttp3.OkHttpClient
import top.pythagodzilla.courser.data.DataStoreManager
import top.pythagodzilla.courser.data.response.TasksApiResponseClass

class OkHttpManager(
    client: OkHttpClient,
    dataStore: DataStoreManager
) : NetworkManager {

    // login相关处理，封装到loginModule
    private val loginModule = LoginModule(client, dataStore)
    private val getInfoModule = GetInfoModule(client)

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

    override suspend fun checkSession(
        deviceUuid: String,
        appVersion: String,
        password: String,
        devicePlatform: String,
        deviceVersion: String,
        username: String,
        deviceName: String
    ) = loginModule.checkSession(
        deviceUuid,
        appVersion,
        password,
        devicePlatform,
        deviceVersion,
        username,
        deviceName
    )

    override suspend fun isSessionValid(sessionId: String) = loginModule.isSessionValid()

    // 获取信息接口的实现，封装到GetInfoModule
    override suspend fun getUndoTasks(): Result<TasksApiResponseClass> = getInfoModule.getUndoTasks()

}