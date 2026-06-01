package top.pythagodzilla.courser.data.dataStore

import androidx.datastore.preferences.core.booleanPreferencesKey

class SettingsPreference {
    val openNotificationKey = booleanPreferencesKey("open_notification")
}