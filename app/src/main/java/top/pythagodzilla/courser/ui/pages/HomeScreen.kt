package top.pythagodzilla.courser.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.pythagodzilla.courser.data.DataStoreManager
import top.pythagodzilla.courser.network.NetworkManager


@Composable
fun HomeScreen(client: NetworkManager, dataStore: DataStoreManager) {
    var testByte by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        Log.d("HomeScreen", "requesting undo tasks")
        var taskInfo = withContext(Dispatchers.IO) {
            client.getUndoTasks()
        }
        taskInfo.onSuccess { response ->
            Log.d(
                "HomeScreen",
                "Undo tasks success: status=${response.status}, courses=${response.datas.size}"
            )
        }.onFailure { error ->
            if (error.message == "Required Login") {
                Log.i("HomeScreen", "${error.message}")
                val (username, password) = dataStore.readLoginInfo()
                if (username == null || password == null) {
                    Result.failure<Exception>(Exception("No login info stored"))
                } else {
                    val session = client.getSessionId(username = username, password = password)
                    session.onSuccess { session ->
                        Log.d("HomeScreen", "Login success, new sessionid: $session")
                        dataStore.saveSessionId(session)
                        taskInfo = client.getUndoTasks()
                    }
                        .onFailure { error ->
                            Log.e("HomeScreen", "Login failed: ${error.message}", error)
                            Result.failure<Exception>(
                                Exception(
                                    "Login failed: ${error.message}",
                                    error
                                )
                            )
                        }
                }
            }
            Log.e("HomeScreen", "Undo tasks failure: ${error.message}", error)
        }
        val errorMessage = taskInfo.exceptionOrNull()?.message.orEmpty()
        testByte = if (errorMessage.contains("status=-2")) {
            "提醒：当前登录已失效（status=-2），请重新登录。\n${taskInfo}"
        } else {
            taskInfo.toString()
        }
        Log.d("HomeScreen", "ui debug text set: ${testByte.take(200)}")
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        Text(testByte)
    }
}
