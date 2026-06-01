package top.pythagodzilla.courser.data.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {
    val session = SessionStore(context)
    val profile = ProfileStore(context)
    val update = UpdateStore()
    val device = DeviceStore(context)

    suspend fun <T> get(key: Preferences.Key<T>): T? =
        context.dataStore.data.map { it[key] }.first()

    suspend fun <T> set(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { it[key] = value }
    }

    suspend fun remove(key: Preferences.Key<*>) {
        context.dataStore.edit { it.remove(key) }
    }
}
