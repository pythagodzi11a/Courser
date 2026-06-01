package top.pythagodzilla.courser.data.dataStore

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DeviceStore(private val context: Context) {
    private val fistStartKey = booleanPreferencesKey("first_start")
    val deviceUuidKey = stringPreferencesKey("device_uuid")

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

    suspend fun detectAndSaveDeviceUuid() {
        @Suppress("HardwareIds")
        val deviceUuid = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown_device_uuid"
        Log.d("DataStoreManager", "Detected device UUID: $deviceUuid")
        context.dataStore.edit { it[deviceUuidKey] = deviceUuid }
    }

}
