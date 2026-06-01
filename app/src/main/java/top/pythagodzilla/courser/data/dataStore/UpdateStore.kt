package top.pythagodzilla.courser.data.dataStore

import androidx.datastore.preferences.core.stringPreferencesKey

class UpdateStore {
    val newestVersionKey = stringPreferencesKey("newest_version")
}
