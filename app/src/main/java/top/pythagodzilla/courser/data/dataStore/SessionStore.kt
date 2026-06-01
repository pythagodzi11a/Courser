package top.pythagodzilla.courser.data.dataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SessionStore(private val context: Context) {
    val sessionKey = stringPreferencesKey("session_id")
    private val usernameKey = stringPreferencesKey("username")
    private val passwordKey = stringPreferencesKey("password")

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
            it.remove(sessionKey)
            it.remove(usernameKey)
            it.remove(passwordKey)
        }
    }
}
