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
import top.pythagodzilla.courser.network.NetworkManager


@Composable
fun HomeScreen(client: NetworkManager) {
    var testByte by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Dispatchers.IO) {
        Log.d("HomeScreen", "requesting undo tasks")
        val TaskInfo = client.getUndoTasks()
        TaskInfo.onSuccess { response ->
            Log.d(
                "HomeScreen",
                "undo tasks success: status=${response.status}, courses=${response.datas.size}"
            )
        }.onFailure { error ->
            Log.e("HomeScreen", "undo tasks failure: ${error.message}", error)
        }
        testByte = TaskInfo.toString()
        Log.d("HomeScreen", "ui debug text set: ${testByte.take(200)}")
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        Text(testByte)
    }
}
