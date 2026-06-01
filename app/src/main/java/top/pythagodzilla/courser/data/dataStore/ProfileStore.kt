package top.pythagodzilla.courser.data.dataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class ProfileStore(private val context: Context) {
    val photoFieldKey = stringPreferencesKey("photo_field")
    val realNameKey = stringPreferencesKey("real_name")
    val loginTimesKey = intPreferencesKey("login_times")

    suspend fun clear() {
        context.dataStore.edit {
            it.remove(photoFieldKey)
            it.remove(realNameKey)
            it.remove(loginTimesKey)
        }
    }
}
