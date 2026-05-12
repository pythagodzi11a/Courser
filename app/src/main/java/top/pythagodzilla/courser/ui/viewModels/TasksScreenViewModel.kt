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
import top.pythagodzilla.courser.data.dataBase.TasksEntities
import top.pythagodzilla.courser.network.exception.SessionExpiredException
import top.pythagodzilla.courser.network.json
import top.pythagodzilla.courser.network.response.ExamClass
import top.pythagodzilla.courser.network.response.HomeworkClass
import top.pythagodzilla.courser.network.response.TasksApiResponseClass
import top.pythagodzilla.courser.ui.types.ExamUIClass
import top.pythagodzilla.courser.ui.types.HomeworkUIClass
import top.pythagodzilla.courser.ui.types.TaskUITypes
import java.time.format.DateTimeFormatter

class TasksScreenViewModel(application: Application) : AndroidViewModel(application) {
    val client = (application as CourserApplication).client
    val dataStore = (application as CourserApplication).dataStore
    val dao = (application as CourserApplication).database.TasksDao()

    private val _tasksUIList = MutableStateFlow<List<TaskUITypes>>(emptyList())
    val tasksUIList: StateFlow<List<TaskUITypes>> = _tasksUIList

    init {
        viewModelScope.launch {
            dao.getAllTasks().collect { entity ->
                _tasksUIList.value = if (entity != null) {
                    parsedTasks(entity.tasksList)
                } else emptyList()
            }
        }

        fetchAndSaveTasks()
    }

    private fun fetchAndSaveTasks() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    client.getUndoTasksString()
                }

                response.onSuccess {
                    dao.insertTasks(TasksEntities(tasksList = it))
                }
                    .onFailure { exception ->
                        when (exception) {
                            is SessionExpiredException -> {
                                withContext(Dispatchers.IO) {
                                    val (username, password) = dataStore.readLoginInfo()

                                    if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                                        val result = client.commonLogin(
                                            username = username,
                                            password = password
                                        )
                                        result.onSuccess { fetchAndSaveTasks() }
                                            .onFailure { }
                                    }
                                }
                            }
                        }
                    }
            } catch (e: Exception) {
                Log.e("OkHttpManager", e.toString())
            }
        }
    }

    private fun parsedTasks(rawJson: String): List<TaskUITypes> {
        val tasks = json.decodeFromString<TasksApiResponseClass>(rawJson)
        val list = mutableListOf<TaskUITypes>()

        tasks.datas.forEach { items ->
            items.reminderList.forEach { task ->
                when (task) {
                    is HomeworkClass -> {
                        list.add(
                            HomeworkUIClass(
                                title = task.title,
                                courseName = items.courseName,
                                startTime = task.startTime.format(
                                    DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm"
                                    )
                                ),
                                endTime = task.endTime.format(
                                    DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm"
                                    )
                                ),
                            )
                        )
                    }

                    is ExamClass -> {
                        list.add(
                            ExamUIClass(
                                title = task.title,
                                courseName = items.courseName,
                                startTime = task.startTime.format(
                                    DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm"
                                    )
                                ),
                                endTime = task.endTime.format(
                                    DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm"
                                    )
                                )
                            )
                        )
                    }
                }
            }
        }

        return list
    }


//    private fun loadTasks() {
//        viewModelScope.launch {
//            try {
//                val result = withContext(Dispatchers.IO) { client.getUndoTasks() }
//
//                result.onSuccess { response ->
//                    val list = mutableListOf<TaskUITypes>()
//                    response.datas.forEach { items ->
//                        items.reminderList.forEach { task ->
//                            when (task) {
//                                is HomeworkClass -> {
//                                    list.add(
//                                        HomeworkUIClass(
//                                            title = task.title,
//                                            courseName = items.courseName,
//                                            startTime = task.startTime.format(
//                                                DateTimeFormatter.ofPattern(
//                                                    "yyyy-MM-dd HH:mm"
//                                                )
//                                            ),
//                                            endTime = task.endTime.format(
//                                                DateTimeFormatter.ofPattern(
//                                                    "yyyy-MM-dd HH:mm"
//                                                )
//                                            ),
//                                        )
//                                    )
//                                }
//
//                                is ExamClass -> {
//                                    list.add(
//                                        ExamUIClass(
//                                            title = task.title,
//                                            courseName = items.courseName,
//                                            startTime = task.startTime.format(
//                                                DateTimeFormatter.ofPattern(
//                                                    "yyyy-MM-dd HH:mm"
//                                                )
//                                            ),
//                                            endTime = task.endTime.format(
//                                                DateTimeFormatter.ofPattern(
//                                                    "yyyy-MM-dd HH:mm"
//                                                )
//                                            )
//                                        )
//                                    )
//                                }
//                            }
//                        }
//                    }
//                    _tasksUIList.value = list
//                }
//                    .onFailure { error ->
//                        Log.e("HomeScreen", "final getUndoTasks failed: ${error.message}", error)
//                        when (error) {
//                            is ClientException -> {
//
//                            }
//
//                            is SessionExpiredException -> {
////                                testByte = "Session expired, please login again."
//                                withContext(Dispatchers.IO) {
//                                    val (username, password) = dataStore.readLoginInfo()
//
//                                    if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
//                                        val result = client.commonLogin(
//                                            username = username,
//                                            password = password
//                                        )
//                                        result.onSuccess { loadTasks() }
//                                            .onFailure { }
//                                    }
//                                }
//                            }
//                        }
//                    }
//            } catch (e: Exception) {
//                Log.e("HomeScreenViewModel", "Failed to load tasks: ${e.message}")
//            }
//        }
//    }
}