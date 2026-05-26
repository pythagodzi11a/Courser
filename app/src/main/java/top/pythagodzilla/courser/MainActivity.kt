package top.pythagodzilla.courser

import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import top.pythagodzilla.courser.data.dataStore.DataStoreManager
import top.pythagodzilla.courser.ui.LoginScreen
import top.pythagodzilla.courser.ui.SplashScreen
import top.pythagodzilla.courser.ui.pages.PageContainer
import top.pythagodzilla.courser.ui.theme.CourserTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dataStore = (application as CourserApplication).dataStore
//        val client = (application as CourserApplication).client

        setContent {
            CourserTheme {
                AppRoot(dataStore)
            }
        }
    }
}


@Composable
fun AppRoot(dataStore: DataStoreManager) {

    // 没有做登录判断，先默认为true，后续要记得改
    var isFirstStart by rememberSaveable { mutableStateOf(true) }
    var haveLoginInfoStatus by rememberSaveable { mutableStateOf(true) }
    var startDestination by rememberSaveable { mutableStateOf<String?>(null) }

    val showUpdateDialog = remember { mutableStateOf(true) }
    val updateInfo = remember { mutableStateOf<UpdateInfo?>(null) }

    val navController = rememberNavController()

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        isFirstStart = withContext(Dispatchers.IO) {
            dataStore.readFirstStart()
        }

        haveLoginInfoStatus = haveLoginInfo(dataStore)

        if (isFirstStart || !haveLoginInfoStatus) {
            // 第一次启动，进入login
            Log.d("AppRoot", "第一次启动，进入login")
            dataStore.detectAndSaveDeviceUuid()

            dataStore.setFirstStart()
            startDestination = "login"
        } else {
            // 已经登录过，进入home
            Log.d("AppRoot", "查询到用户信息，进入home")
            startDestination = "pages"
        }

        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(Unit) {
        val updateInfoFetch = checkSoftwareUpdate(dataStore)
        if (updateInfoFetch != null) {
            Log.d("AppRoot", "发现新版本: ${updateInfoFetch.tagName}")
            updateInfo.value = updateInfoFetch
            showUpdateDialog.value = true
        }
    }

    if (startDestination == null) {
        SplashScreen()
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination!!,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable("login") { LoginScreen(navController = navController) }
            composable("pages") { PageContainer(navController = navController) }
        }

        if (showUpdateDialog.value && updateInfo.value != null) {
            UpdateDialog(
                onConfirmation = {
                    showUpdateDialog.value = false
                    downloadApk(context, updateInfo.value!!.downloadUrl, updateInfo.value!!)
                },
                onDismissRequest = {
                    showUpdateDialog.value = false
                },
                updateInfo = updateInfo.value!!,
            )
        }
    }
}

suspend fun haveLoginInfo(dataStore: DataStoreManager): Boolean {
    return withContext(Dispatchers.IO) {
        val (username, password) = dataStore.readLoginInfo()
        !username.isNullOrBlank() && !password.isNullOrBlank()
    }
}

data class UpdateInfo(
    val tagName: String,
    val downloadUrl: String,
    val releaseNotes: String
)

suspend fun checkSoftwareUpdate(dataStore: DataStoreManager): UpdateInfo? {
    val client = OkHttpClient()
    var result: UpdateInfo? = null

    try {
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api.github.com/repos/pythagodzi11a/Courser/releases/latest")
                .header("Accept", "application/vnd.github.v3+json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("UpdateCheck", "Failed to check for updates: ${response.code}")
                    return@withContext null
                }

                val responseBody = response.body.string()
                val jsonObj = JSONObject(responseBody)
                val tagName = jsonObj.getString("tag_name")

                val currentVersion = dataStore.readNewestVersion()


                when (currentVersion) {
                    null -> {
                        dataStore.saveNewestVersion(tagName)
                    }

                    tagName -> {
                        Log.d("UpdateCheck", "Already have the latest version: $tagName")
                    }

                    else -> {
                        Log.d("UpdateCheck", "New version available: $tagName")
                        dataStore.saveNewestVersion(tagName)
                        result = UpdateInfo(
                            tagName = tagName,
                            downloadUrl = jsonObj.getJSONArray("assets").getJSONObject(0)
                                .getString("browser_download_url"),
                            releaseNotes = jsonObj.getString("body")
                        )
                    }
                }

            }

        }
    } catch (e: Exception) {
        Log.e("UpdateCheck", "Error checking for updates: ${e.message}")
    }
    return result
}

fun downloadApk(context: Context, url: String, updateInfo: UpdateInfo) {
    val request = DownloadManager.Request(url.toUri())
        .setTitle("Courser 更新")
        .setDescription("正在下载 Courser 的最新版本")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "Courser_${updateInfo.tagName}.apk"
        )
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)

    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    dm.enqueue(request)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    updateInfo: UpdateInfo,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Update, contentDescription = "Example Icon")
        },
        title = {
            Text(text = "发现新版本")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "新版本已发布：${updateInfo.tagName}")

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .heightIn(min = 100.dp, max = 300.dp)
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = updateInfo.releaseNotes,
                    )
                }
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("下载")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("取消")
            }
        }
    )
}