package top.pythagodzilla.courser.ui

import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.pythagodzilla.courser.data.DataStoreManager
import top.pythagodzilla.courser.network.NetworkManager

@Composable
fun LoginScreen(client: NetworkManager, dataStore: DataStoreManager) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var sessionString by rememberSaveable { mutableStateOf("") }

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
                        sessionString = "请求中"
                        val result = withContext(Dispatchers.IO) {
                            client.getSessionId(
                                username = username,
                                password = password
                            )
                        }
                        sessionString = result.fold(
                            onSuccess = { sessionId ->
                                // 暂时存在这留着调试，以后会删除
                                Log.d("DataStore", "session store: $sessionId")
                                dataStore.saveSessionId(sessionId)
                                "Session ID: $sessionId"
                            },
                            onFailure = { response ->
                                Log.d("DataStore", " session get failed")
                                "登录失败: ${response.message}"
                            }
                        )

                        dataStore.addLoginInfo(username, password)
                        Log.d("LoginScreen", "登录结果: $")
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("登录")
            }

            Text(sessionString)

        }
    }
}
