package top.pythagodzilla.courser.ui.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.pythagodzilla.courser.CourserApplication
import top.pythagodzilla.courser.network.exception.ClientException
import top.pythagodzilla.courser.network.exception.SessionExpiredException
import top.pythagodzilla.courser.network.response.ExamClass
import top.pythagodzilla.courser.network.response.HomeworkClass
import top.pythagodzilla.courser.ui.types.ExamUIClass
import top.pythagodzilla.courser.ui.types.HomeworkUIClass
import top.pythagodzilla.courser.ui.types.TaskUITypes

class HomeScreenViewModel(application: Application) : AndroidViewModel(application) {
    val client = (application as CourserApplication).client
    val dataStore = (application as CourserApplication).dataStore

    private val _tasksUIList = MutableStateFlow<List<TaskUITypes>>(emptyList())
    val tasksUIList: StateFlow<List<TaskUITypes>> = _tasksUIList

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { client.getUndoTasks() }

                result.onSuccess { response ->
                    val list = mutableListOf<TaskUITypes>()
                    response.datas.forEach { items ->
                        items.reminderList.forEach { task ->
                            when (task) {
                                is HomeworkClass -> {
                                    list.add(
                                        HomeworkUIClass(
                                            title = task.title,
                                            courseName = items.courseName,
                                            startTime = task.startTime.toString(),
                                            endTime = task.endTime.toString(),
                                        )
                                    )
                                }

                                is ExamClass -> {
                                    list.add(
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
                    _tasksUIList.value = list
                }
                    .onFailure { error ->
                        Log.e("HomeScreen", "final getUndoTasks failed: ${error.message}", error)
                        when (error) {
                            is ClientException -> {

                            }

                            is SessionExpiredException -> {
//                                testByte = "Session expired, please login again."
                                withContext(Dispatchers.IO) {
                                    val (username, password) = dataStore.readLoginInfo()

                                    if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                                        val result = client.commonLogin(
                                            username = username,
                                            password = password
                                        )
                                        result.onSuccess { loadTasks() }
                                            .onFailure { }
                                    }
                                }
                            }
                        }
                    }
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Failed to load tasks: ${e.message}")
            }
        }
    }
}