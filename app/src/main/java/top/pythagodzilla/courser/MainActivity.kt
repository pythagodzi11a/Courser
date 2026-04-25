package top.pythagodzilla.courser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.pythagodzilla.courser.data.DataStore
import top.pythagodzilla.courser.network.OkHttpManager
import top.pythagodzilla.courser.ui.home.HomeScreen
import top.pythagodzilla.courser.ui.login.LoginScreen
import top.pythagodzilla.courser.ui.theme.CourserTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val dataStore = DataStore(applicationContext)

        setContent {
            CourserTheme {
                AppRoot(dataStore)
            }
        }
    }
}


@Composable
fun AppRoot(dataStore: DataStore) {

    // 没有做登录判断，先默认为true，后续要记得改
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var isFirstStart by rememberSaveable { mutableStateOf(true) }
    var shouldShowLogin by rememberSaveable { mutableStateOf(true) }

    val client = remember { OkHttpManager() }

    LaunchedEffect(Unit) {
        isFirstStart = withContext(Dispatchers.IO) {
            dataStore.readFirstStart()
        }
    }

    // 所有的判断都是为了shouldShowLogin状态，决定开启是否直接进入Login。
    if (isFirstStart) {
        // 第一次打开，直接去登录界面
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                dataStore.setFirstStart()

                shouldShowLogin = true
            }
        }

    } else {
        // 已经不是第一次打开应用
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                val currentSession = dataStore.readSessionId()
                shouldShowLogin = if (currentSession.isNullOrBlank()) {
                    true
                } else {
                    withContext(Dispatchers.IO) {
                        // 有效就直接进入主页，无效就去登录界面
                        !client.isSessionValid(currentSession)
                    }
                }
            }
        }
        // 所有东西都准备好了，直接进入主页
        shouldShowLogin = false
    }

    if (shouldShowLogin) {
        LoginScreen(client)
    } else {
        HomeScreen(client)
    }
}