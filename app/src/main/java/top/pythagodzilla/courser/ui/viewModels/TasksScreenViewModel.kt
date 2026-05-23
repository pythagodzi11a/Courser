package top.pythagodzilla.courser.ui.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import top.pythagodzilla.courser.CourserApplication
import top.pythagodzilla.courser.data.dataBase.TaskDetailEntity
import top.pythagodzilla.courser.data.dataBase.TasksEntities
import top.pythagodzilla.courser.network.exception.SessionExpiredException
import top.pythagodzilla.courser.network.json
import top.pythagodzilla.courser.network.response.ExamResponseClass
import top.pythagodzilla.courser.network.response.HomeworkResponseClass
import top.pythagodzilla.courser.network.response.HomeworkViewResponse
import top.pythagodzilla.courser.network.response.TasksApiResponseClass
import top.pythagodzilla.courser.ui.types.ExamClass
import top.pythagodzilla.courser.ui.types.HomeworkClass
import top.pythagodzilla.courser.ui.types.TasksType
import java.time.format.DateTimeFormatter

class TasksScreenViewModel(application: Application) : AndroidViewModel(application) {
    enum class SortMode {
        BY_START_TIME,
        BY_END_TIME,
        BY_COURSE_NAME,
    }

    val client = (application as CourserApplication).client
    val dataStore = (application as CourserApplication).dataStore
    val tasksDao = (application as CourserApplication).database.TasksDao()
    val taskDetailDao = (application as CourserApplication).database.TaskDetailDao()

    private val _tasksUIList = MutableStateFlow<List<TasksType>>(emptyList())
    val tasksUIList: StateFlow<List<TasksType>> = _tasksUIList

    private val detailMutex = Mutex()

    private val _sortMode = MutableStateFlow(SortMode.BY_END_TIME)
    val sortMode: StateFlow<SortMode> = _sortMode

    init {
        viewModelScope.launch {
            combine(tasksDao.getAllTasks(), _sortMode) { entity, mode ->
                val list = if (entity != null) parsedTasks(entity.tasksList) else emptyList()
                when (mode) {
                    SortMode.BY_END_TIME -> list.sortedBy { it.endTime }
                    SortMode.BY_COURSE_NAME -> list.sortedBy { it.courseName }
                    SortMode.BY_START_TIME -> list.sortedBy { it.startTime }
                }
            }.collect { _tasksUIList.value = it }
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
                    tasksDao.insertTasks(TasksEntities(tasksList = it))
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

    private fun parsedTasks(rawJson: String): List<TasksType> {
        val tasks = json.decodeFromString<TasksApiResponseClass>(rawJson)
        val list = mutableListOf<TasksType>()


        tasks.datas.forEach { items ->
            items.reminderList.forEach { task ->
                when (task) {
                    is HomeworkResponseClass -> {
                        list.add(
                            HomeworkClass(
                                id = task.id.toString(),
                                courseId = items.courseId.toString(),
                                taskTitle = task.title,
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

                    is ExamResponseClass -> {
                        list.add(
                            ExamClass(
                                id = task.id.toString(),
                                courseId = items.courseId.toString(),
                                taskTitle = task.title,
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

    suspend fun getTasksDetails(courseId: String, hwtid: String): HomeworkViewResponse? {
        Log.d(
            "TasksScreenViewModel",
            "Fetching task details for courseId: $courseId, hwtid: $hwtid"
        )
        val cached = taskDetailDao.getTaskDetailByTaskId(hwtid, courseId)
        if (cached != null) {
            Log.d(
                "TasksScreenViewModel",
                "Found cached task detail for hwtid: $hwtid, courseId: $courseId, cached : ${cached.taskDetail}"
            )
            return json.decodeFromString<HomeworkViewResponse>(cached.taskDetail)
        }

        detailMutex.withLock {
            val enterCourseResponse = withContext(Dispatchers.IO) {
                client.enterCourse(courseId)
            }

            val homeworkViewResponse = withContext(Dispatchers.IO) {
                client.getHomeworkView(hwtid, courseId)
            }

            homeworkViewResponse.onSuccess { response ->
                val parsedResponse = parsedTaskDetailResponse(response)

                if (parsedResponse == null) {
                    Log.e("TasksScreenViewModel", "Failed to parse homework view response")
                    return@onSuccess
                }

                Log.d(
                    "TasksScreenViewModel",
                    "Homework view response: ${parsedResponse.datas.taskContent}"
                )
                taskDetailDao.insertTaskDetail(
                    TaskDetailEntity(
                        taskId = hwtid,
                        courseId = courseId,
                        taskDetail = response
                    )
                )
                return parsedResponse
            }
                .onFailure { exception ->
                    Log.e(
                        "TasksScreenViewModel",
                        "Failed to get homework view: ${exception.message}"
                    )
                }
        }
        return null
    }

    fun parsedTaskDetailResponse(rawJson: String): HomeworkViewResponse? {
        return try {
            json.decodeFromString<HomeworkViewResponse>(rawJson)
        } catch (e: Exception) {
            Log.e("TasksScreenViewModel", "Failed to parse homework view response: ${e.message}")
            null
        }
    }

    fun setSortMode(mode: SortMode) {
        _sortMode.value = mode
    }

}