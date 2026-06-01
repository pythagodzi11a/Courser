package top.pythagodzilla.courser.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.pythagodzilla.courser.CourserApplication

class ProfileScreenViewModel(application: Application) :
    AndroidViewModel(application) {
    val dataStore = (application as CourserApplication).dataStore

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl

    private val _loginTimes = MutableStateFlow<Int?>(null)
    val loginTimes: StateFlow<Int?> = _loginTimes

    private val _realName = MutableStateFlow<String?>(null)
    val realName: StateFlow<String?> = _realName

    private val _openNotification = MutableStateFlow<Boolean?>(null)
    val openNotification: StateFlow<Boolean?> = _openNotification

    init {
        viewModelScope.launch {
            _avatarUrl.value = dataStore.get(dataStore.profile.photoFieldKey)
            _loginTimes.value = dataStore.get(dataStore.profile.loginTimesKey)
            _realName.value = dataStore.get(dataStore.profile.realNameKey)
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataStore.session.clearLoginInfo()
                dataStore.profile.clear()
            }
            onDone()
        }
    }
}