package top.pythagodzilla.courser.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {
    private val sessionKey = stringPreferencesKey("session_id")
    private val usernameKey = stringPreferencesKey("username")
    private val passwordKey = stringPreferencesKey("password")
    private val fistStartKey = booleanPreferencesKey("first_start")

//    val sessionFlow: Flow<String?> = context.dataStore.data.map { it[sessionKey] }


    suspend fun saveSessionId(sessionId: String) {
        context.dataStore.edit { it[sessionKey] = sessionId }
    }

    suspend fun clearSessionId() {
        context.dataStore.edit { it.remove(sessionKey) }
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

    suspend fun readLoginInfo(): Boolean {
        val username = context.dataStore.data.map { it[usernameKey] }.first()
        val password = context.dataStore.data.map { it[passwordKey] }.first()

        return !username.isNullOrBlank() && !password.isNullOrBlank()
    }

    suspend fun addLoginInfo(username: String, password: String) {
        context.dataStore.edit {
            it[usernameKey] = username
            it[passwordKey] = password
        }
    }
}