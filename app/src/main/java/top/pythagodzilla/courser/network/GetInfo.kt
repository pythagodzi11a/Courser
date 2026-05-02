package top.pythagodzilla.courser.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import top.pythagodzilla.courser.network.exception.HttpException
import top.pythagodzilla.courser.network.exception.SessionExpiredException
import top.pythagodzilla.courser.network.exception.UnknownException
import top.pythagodzilla.courser.network.response.TasksApiResponseClass
import top.pythagodzilla.courser.network.response.checkResponseNotLogin

class GetInfoModule(private val client: OkHttpClient = OkHttpClient()) {

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
                val finalResponse = checkResponseNotLogin(response)

                finalResponse
                    .onSuccess { response ->

                        return Result.success(json.decodeFromString<TasksApiResponseClass>(response.body.string()))
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
            return Result.failure(Exception("Failed to get undo tasks: ${e.message}", e))
        }
    }
}
