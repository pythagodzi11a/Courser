package top.pythagodzilla.courser.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.pythagodzilla.courser.network.OkHttpManager

@Composable
fun LoginScreen(client: OkHttpManager) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var sessionString by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()

//    val client = remember { OkHttpManager() }

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
                    sessionString = "请求中"
                    val result = withContext(Dispatchers.IO) {
                        client.getSessionId(
                            username = username,
                            password = password
                        )
                    }
                    sessionString = result.fold(
                        onSuccess = { "Session ID: $it" },
                        onFailure = { "登录失败: ${it.message}" }
                    )
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("登录")
        }

        Text(sessionString)

    }
}
