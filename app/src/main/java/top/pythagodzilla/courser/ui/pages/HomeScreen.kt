package top.pythagodzilla.courser.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.pythagodzilla.courser.data.DataStoreManager
import top.pythagodzilla.courser.network.NetworkManager
import top.pythagodzilla.courser.network.exception.ClientException
import top.pythagodzilla.courser.network.exception.SessionExpiredException
import top.pythagodzilla.courser.network.response.ExamClass
import top.pythagodzilla.courser.network.response.HomeworkClass
import top.pythagodzilla.courser.network.response.TasksApiResponseClass
import top.pythagodzilla.courser.ui.composable.TaskCard
import top.pythagodzilla.courser.ui.types.ExamUIClass
import top.pythagodzilla.courser.ui.types.HomeworkUIClass
import top.pythagodzilla.courser.ui.types.TaskUITypes


@Composable
fun HomeScreen(client: NetworkManager, dataStore: DataStoreManager) {
    var testByte by rememberSaveable { mutableStateOf("") }
    val tasksUIList = remember { mutableStateListOf<TaskUITypes>() }

    suspend fun fetchAndRenderTasks() {
        Log.d("HomeScreen", "requesting undo tasks")
        withContext(Dispatchers.IO) {
            val result = client.getUndoTasks()

            result.onSuccess { response ->
                Log.d(
                    "HomeScreen",
                    "Undo tasks success: status=${response.status}, courses=${response.datas.size}"
                )
                tasksUIList.clear()
                response.datas.forEach { items ->
                    items.reminderList.forEach { task ->
                        when (task) {
                            is HomeworkClass -> {
                                tasksUIList.add(
                                    HomeworkUIClass(
                                        title = task.title,
                                        courseName = items.courseName,
                                        startTime = task.startTime.toString(),
                                        endTime = task.endTime.toString(),
                                    )
                                )
                            }

                            is ExamClass -> {
                                tasksUIList.add(
                                    ExamUIClass(
                                        title = task.title,
                                        courseName = items.courseName,
                                        startTime = task.startTime.toString(),
                                        endTime = task.endTime.toString()
                                    )
                                )
                            }
                        }

                    }
                }
            }.onFailure { error ->
                Log.e("HomeScreen", "final getUndoTasks failed: ${error.message}", error)
                when (error) {
                    is ClientException -> {
                        testByte = "Client time out"
                    }

                    is SessionExpiredException -> {
                        testByte = "Session expired, please login again."
                        withContext(Dispatchers.IO) {
                            val (username, password) = dataStore.readLoginInfo()

                            if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                                val result = client.commonLogin(
                                    username = username,
                                    password = password
                                )
                                result.onSuccess { fetchAndRenderTasks() }
                                    .onFailure { }
                            }
                        }
                    }
                }
            }

            Log.d("HomeScreen", "ui debug text set: ${testByte.take(200)}")
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            fetchAndRenderTasks()
        }
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        Text(text = testByte)
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(tasksUIList.size) {
                TaskCard(tasksUIList[it])
            }
        }
    }


}
