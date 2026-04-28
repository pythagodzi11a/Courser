package top.pythagodzilla.courser.network

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import top.pythagodzilla.courser.data.types.TasksApiResponseClass

class GetInfoModule(private val client: OkHttpClient = OkHttpClient()) {
    suspend fun getUndoTasks(): Result<TasksApiResponseClass> {
        val json = Json { ignoreUnknownKeys = true }
        val request = Request.Builder()
            .url("http://course.buct.edu.cn/mobile/stuUnDoTaskList.do")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Result.failure<Exception>(Exception("HTTP ${response.code}: ${response.body.string()}"))
                }

                val content = response.body.string()
                val result = json.decodeFromString<TasksApiResponseClass>(content)

                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get undo tasks: ${e.message}", e))
        }
    }
}