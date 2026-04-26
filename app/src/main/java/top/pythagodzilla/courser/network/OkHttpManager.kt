package top.pythagodzilla.courser.network

import okhttp3.OkHttpClient
import top.pythagodzilla.courser.data.DataStoreManager

class OkHttpManager(
    client: OkHttpClient,
    dataStore: DataStoreManager
) : NetworkManager {
    private val loginModule = LoginModule(client, dataStore)

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

    override suspend fun isSessionValid(sessionId: String) = loginModule.isSessionValid(sessionId)
}