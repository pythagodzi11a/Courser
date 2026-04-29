package top.pythagodzilla.courser

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import top.pythagodzilla.courser.data.DataStoreManager
import top.pythagodzilla.courser.network.NetworkManager
import top.pythagodzilla.courser.network.OkHttpManager
import top.pythagodzilla.courser.network.SessionCookieInterceptor
import top.pythagodzilla.courser.ui.LoginScreen
import top.pythagodzilla.courser.ui.SplashScreen
import top.pythagodzilla.courser.ui.pages.PageContainer
import top.pythagodzilla.courser.ui.theme.CourserTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dataStore = DataStoreManager(applicationContext)
        val client = OkHttpClient.Builder()
            .addInterceptor(SessionCookieInterceptor(dataStore))
            .build()

        setContent {
            CourserTheme {
                AppRoot(dataStore, client)
            }
        }
    }
}


@Composable
fun AppRoot(dataStore: DataStoreManager, httpClient: OkHttpClient) {

    // 没有做登录判断，先默认为true，后续要记得改
    var isFirstStart by rememberSaveable { mutableStateOf(true) }
    var haveLoginInfoStatus by rememberSaveable { mutableStateOf(true) }
    var startDestination by rememberSaveable { mutableStateOf<String?>(null) }

    val client: NetworkManager =
        remember { OkHttpManager(client = httpClient, dataStore = dataStore) }

    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        isFirstStart = withContext(Dispatchers.IO) {
            dataStore.readFirstStart()
        }

        haveLoginInfoStatus = haveLoginInfo(dataStore)

//        delay(1000)

        if (isFirstStart || !haveLoginInfoStatus) {
            // 第一次启动，进入login
            Log.d("AppRoot", "第一次启动，进入login")

            dataStore.setFirstStart()
            startDestination = "login"
        } else {
            // 已经登录过，进入home
            Log.d("AppRoot", "查询到用户信息，进入home")
            startDestination = "page"
        }
    }

    if (startDestination == null) {
        SplashScreen()
        return
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination!!,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginScreen(client, dataStore) }
            composable("page") { PageContainer(client, dataStore) }
        }
    }
}

suspend fun haveLoginInfo(dataStore: DataStoreManager): Boolean {
    return withContext(Dispatchers.IO) {
        val (username, password) = dataStore.readLoginInfo()
        !username.isNullOrBlank() && !password.isNullOrBlank()
    }
}

