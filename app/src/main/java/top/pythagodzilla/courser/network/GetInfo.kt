package top.pythagodzilla.courser.network

import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import top.pythagodzilla.courser.data.types.TasksApiResponseClass

class GetInfoModule(private val client: OkHttpClient = OkHttpClient()) {
    suspend fun getUndoTasks(): Result<TasksApiResponseClass> {
        Log.d("GetInfoModule", "getUndoTasks start")
        val json = Json { ignoreUnknownKeys = true }
        val request = Request.Builder()
            .url("http://course.buct.edu.cn/mobile/stuUnDoTaskList.do")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                val content = response.body.string()
                Log.d("GetInfoModule", "httpCode=${response.code}, bodyLength=${content.length}")
                Log.d("GetInfoModule", "responsePreview=${content.take(300)}")

                if (!response.isSuccessful) {
                    Log.e("GetInfoModule", "request failed: HTTP ${response.code}")
                    return Result.failure(Exception("HTTP ${response.code}: $content"))
                }

                Log.d("GetInfoModule", "start decode TasksApiResponseClass")
                val result = json.decodeFromString<TasksApiResponseClass>(content)
                Log.d(
                    "GetInfoModule",
                    "decode success: status=${result.status}, sessionid=${result.sessionid}, courses=${result.datas.size}"
                )

                Result.success(result)
            }
        } catch (e: Exception) {
            Log.e("GetInfoModule", "getUndoTasks exception: ${e::class.java.simpleName}: ${e.message}", e)
            Result.failure(Exception("Failed to get undo tasks: ${e.message}", e))
        }
    }
}
