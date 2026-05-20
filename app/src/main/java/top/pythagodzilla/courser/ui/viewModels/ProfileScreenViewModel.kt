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

    init {
        viewModelScope.launch {
            _avatarUrl.value = dataStore.readPhotoField()
            _loginTimes.value = dataStore.readLoginTimes()
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataStore.clearLoginInfo()
            }
            onDone()
        }
    }
}