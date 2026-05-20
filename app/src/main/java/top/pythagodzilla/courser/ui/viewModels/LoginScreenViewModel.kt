package top.pythagodzilla.courser.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.pythagodzilla.courser.CourserApplication

class LoginScreenViewModel(application: Application) : AndroidViewModel(application) {
    val client = (application as CourserApplication).client
    val dataStore = (application as CourserApplication).dataStore

    val buttonLoading = MutableStateFlow(false)
    val loginMessage = MutableStateFlow("")
    val loginStatus = MutableStateFlow(false)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            buttonLoading.value = true
            loginMessage.value = ""

            withContext(Dispatchers.IO) {
                val result = client.commonLogin(
                    username = username,
                    password = password
                )

                result.onSuccess { result ->
                    loginMessage.value = result
                    loginStatus.value = true
                    dataStore.addLoginInfo(username, password)
                }
                    .onFailure {
                        loginMessage.value = it.message ?: it.toString()
                    }
            }

            buttonLoading.value = false
        }
    }
}