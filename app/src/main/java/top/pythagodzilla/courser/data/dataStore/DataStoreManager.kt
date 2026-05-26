package top.pythagodzilla.courser.data.dataStore

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {
    private val newestVersionKey = stringPreferencesKey("newest_version")
    private val sessionKey = stringPreferencesKey("session_id")
    private val usernameKey = stringPreferencesKey("username")
    private val passwordKey = stringPreferencesKey("password")
    private val fistStartKey = booleanPreferencesKey("first_start")
    private val deviceUuidKey = stringPreferencesKey("device_uuid")

    // User info.
    private val photoFieldKey = stringPreferencesKey("photo_field")
    private val realNameKey = stringPreferencesKey("real_name")
    private val loginTimesKey = intPreferencesKey("login_times")

    suspend fun saveSessionId(sessionId: String) {
        context.dataStore.edit { it[sessionKey] = sessionId }
    }

    suspend fun readSessionId(): String? {
        return context.dataStore.data.map { it[sessionKey] }.first()
    }

    suspend fun readFirstStart(): Boolean {
        val firstStart = context.dataStore.data.map {
            it[fistStartKey] ?: true
        }.first()

        return firstStart
    }

    suspend fun setFirstStart(isFirstStart: Boolean = false) {
        val firstStartKey = booleanPreferencesKey("first_start")
        context.dataStore.edit { it[firstStartKey] = isFirstStart }
    }

    suspend fun readLoginInfo(): Pair<String?, String?> {
        val username = context.dataStore.data.map { it[usernameKey] }.first()
        val password = context.dataStore.data.map { it[passwordKey] }.first()

        return Pair(username, password)
    }

    suspend fun addLoginInfo(username: String, password: String) {
        context.dataStore.edit {
            it[usernameKey] = username
            it[passwordKey] = password
        }
    }

    suspend fun clearLoginInfo() {
        context.dataStore.edit {
            it.remove(usernameKey)
            it.remove(passwordKey)
            it.remove(sessionKey)
            it.remove(photoFieldKey)
            it.remove(realNameKey)
            it.remove(loginTimesKey)
        }
    }

    suspend fun detectAndSaveDeviceUuid() {

        @Suppress("HardwareIds")
        val deviceUuid = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown_device_uuid"
        Log.d("DataStoreManager", "Detected device UUID: $deviceUuid")
        context.dataStore.edit { it[deviceUuidKey] = deviceUuid }
    }

    suspend fun readDeviceUuid(): String? {
        return context.dataStore.data.map { it[deviceUuidKey] }.first()
    }

    suspend fun savePhotoField(photoField: String) {
        context.dataStore.edit { it[photoFieldKey] = photoField }
    }

    suspend fun readPhotoField(): String? {
        return context.dataStore.data.map { it[photoFieldKey] }.first()
    }

    suspend fun saveRealName(realName: String) {
        context.dataStore.edit { it[realNameKey] = realName }
    }

    suspend fun readRealName(): String? {
        return context.dataStore.data.map { it[realNameKey] }.first()
    }

    suspend fun saveLoginTimes(loginTimes: Int) {
        context.dataStore.edit { it[loginTimesKey] = loginTimes }
    }

    suspend fun readLoginTimes(): Int? {
        return context.dataStore.data.map { it[loginTimesKey] }.first()
    }

    suspend fun readNewestVersion(): String? {
        return context.dataStore.data.map { it[newestVersionKey] }.first()
    }

    suspend fun saveNewestVersion(version: String) {
        context.dataStore.edit { it[newestVersionKey] = version }
    }
}