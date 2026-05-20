package top.pythagodzilla.courser.network

import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import top.pythagodzilla.courser.network.exception.ClientException
import top.pythagodzilla.courser.network.exception.HttpException
import top.pythagodzilla.courser.network.exception.SessionExpiredException
import top.pythagodzilla.courser.network.exception.UnknownException
import top.pythagodzilla.courser.network.response.TasksApiResponseClass
import top.pythagodzilla.courser.network.response.checkResponseNotLogin

class GetInfoModule(private val client: OkHttpClient = OkHttpClient()) {

    /**
     * 获取待办任务列表
     * @return Result<String>
     */
    suspend fun getUndoTasksString(): Result<String> {
        Log.d("GetInfoModule", "Start get undo tasks")

        val request = Request.Builder()
            .url("http://course.buct.edu.cn/mobile/stuUnDoTaskList.do")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val content = response.body.string()
                val finalContent = checkResponseNotLogin(content)

                finalContent.onSuccess {
                    return Result.success(it)
                }.onFailure {
                    return Result.failure(it)
                }

                return Result.failure(UnknownException(response.code, response.body.toString()))
            }
        } catch (e: Exception) {
            Log.e("GetInfoModule", e.toString())
            return Result.failure(UnknownException(-1, e.toString()))
        }
    }

    /**
     * 获取待办任务列表
     * @return Result 包含 TasksApiResponseClass 或异常
     */
    suspend fun getUndoTasks(): Result<TasksApiResponseClass> {
        Log.d("GetInfoModule", "getUndoTasks start")

        val request = Request.Builder()
            .url("http://course.buct.edu.cn/mobile/stuUnDoTaskList.do")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val content = response.body.string()

                val finalContent = checkResponseNotLogin(content)

                finalContent
                    .onSuccess { currentContent ->

                        return Result.success(
                            json.decodeFromString<TasksApiResponseClass>(
                                currentContent
                            )
                        )
                    }
                    .onFailure { exception ->
                        when (exception) {
                            is HttpException -> {
                                Log.e(
                                    "GetInfoModule",
                                    "HTTP error occurred: ${exception.code}: ${exception.message}"
                                )
                                return Result.failure(exception)
                            }

                            is SessionExpiredException -> {
                                Log.e(
                                    "GetInfoModule",
                                    "Session expired: ${exception.code}: ${exception.message}"
                                )
                                return Result.failure(exception)
                            }

                            is UnknownException -> {
                                Log.e(
                                    "GetInfoModule",
                                    "Unknown error occurred: ${exception.code}: ${exception.message}"
                                )
                                return Result.failure(exception)
                            }
                        }
                    }

                return Result.failure(UnknownException(response.code, "Unexpected response format"))
            }
        } catch (e: Exception) {
            Log.e(
                "GetInfoModule",
                "getUndoTasks exception: ${e::class.java.simpleName}: ${e.message}",
                e
            )
            return Result.failure(ClientException("Failed to get undo tasks: ${e.message}"))
        }
    }

    /**
     * enterCourse接口，用来通知服务器进行状态管理。
     * @param courseId String 课程ID
     * @return Result<String>，成功时返回原始body以供反序列化，失败时返回异常
     */
    suspend fun enterCourse(courseId: String): Result<String> {
        val body = FormBody.Builder()
            .add("courseId", courseId)
            .build()

        val request = Request.Builder()
            .url("http://course.buct.edu.cn/mobile/enterCourse.do")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val content = checkResponseNotLogin(response.body.string())
                    content
                        .onSuccess {
                            return Result.success(it)
                        }
                        .onFailure {
                            return Result.failure(
                                SessionExpiredException(
                                    response.code,
                                    it.message ?: "Session expired",
                                    response.code
                                )
                            )
                        }
                    return Result.failure(
                        UnknownException(
                            response.code,
                            "Unexpected response format"
                        )
                    )
                } else {
                    return Result.failure(
                        HttpException(
                            response.code,
                            "HTTP error: ${response.message}"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(
                "GetInfoModule", "enterCourse exception: ${e.toString()}"
            )
            return Result.failure(HttpException(-1, "Failed to enter course: ${e.message}"))
        }
    }

    suspend fun getHomeworkView(hwtid: String, context: String): Result<String> {
        val body = FormBody.Builder()
            .add("hwtid", hwtid)
            .add("context", context)
            .build()

        val request = Request.Builder()
            .url("http://course.buct.edu.cn/mobile/homeworkView.do")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
//                val content = checkResponseNotLogin(response.body.string())
//
//                content.onSuccess { return Result.success(it) }
//                    .onFailure { }
                val content = response.body.string()
                Log.d("GetInfoModule", "getHomeworkView raw response: $content")
                return Result.success(content)
            }
        } catch (e: Exception) {
            Log.e(
                "GetInfoModule", "getHomeworkView exception: ${e.toString()}"
            )
            return Result.failure(
                HttpException(
                    -1,
                    "Failed to get homework view: ${e.message}"
                )
            )
        }
    }
}
