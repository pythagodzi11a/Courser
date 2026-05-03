package top.pythagodzilla.courser.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.pythagodzilla.courser.data.DataStoreManager
import top.pythagodzilla.courser.network.NetworkManager

@Composable
fun LoginScreen(client: NetworkManager, dataStore: DataStoreManager, navController: NavController) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }

    var buttonLoading by rememberSaveable { mutableStateOf(false) }
    var loginStatus by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()


    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("登录")
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("用户名") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("密码")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {

                        buttonLoading = true

                        withContext(Dispatchers.IO) {
                            val result = client.commonLogin(
                                username = username,
                                password = password
                            )

                            result.onSuccess { result ->
                                message = result
                                loginStatus = true
                                dataStore.addLoginInfo(username, password)
                            }
                                .onFailure {
                                    message = it.message ?: it.toString()
                                }
                        }
                        buttonLoading = false
                    }
                },
                enabled = !buttonLoading,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                if (loginStatus) {
                    navController.navigate("pages")
                }

                if (buttonLoading) Text("登录中...")
                else Text("登录")
            }

            Text(message)

        }
    }
}

fun toLogin() {

}