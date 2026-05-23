package top.pythagodzilla.courser.ui.pages

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.brands.Qq
import top.pythagodzilla.courser.R
import top.pythagodzilla.courser.ui.types.SettingUITypes
import top.pythagodzilla.courser.ui.viewModels.ProfileScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileScreenViewModel: ProfileScreenViewModel = viewModel(),
    navController: NavController
) {
    val photoField by profileScreenViewModel.avatarUrl.collectAsStateWithLifecycle()
    val realName by profileScreenViewModel.realName.collectAsState()
    val loginTimes by profileScreenViewModel.loginTimes.collectAsState()

    val settingsList = listOf(
        SettingUITypes.Toggle(
            title = "夜间模式",
            icon = Icons.Default.AcUnit,
            contentDescription = "夜间模式",
            checked = false,
            onChecked = {}
        )
    )

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val builder = NotificationCompat.Builder(context, "task_reminder")
            .setSmallIcon(R.drawable.ic_favorite)
            .setContentTitle("设置")
            .setContentText("这是一个设置的通知")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(1, builder.build())
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Text(
                text = "Courser",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 30.sp,
                modifier = Modifier.padding(16.dp)
            )

        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {


            if (photoField.isNullOrEmpty()) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Icon",
                    modifier = Modifier
                        .size(128.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                AsyncImage(
//                model = "http://course.buct.edu.cn/mobile/common/ckeditor/openminfile.jsp?id=DBDEDEDHDGDACPDHDBDIDEDHDDDDCPDHDBDIDEDHDDDDCOGKHAGH",
                    model = "http://course.buct.edu.cn/mobile/common/ckeditor/openphonefile.jsp?id=$photoField",
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(RoundedCornerShape(50))
                        .align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    text = "你好，${realName}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp),
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "登录次数：${loginTimes}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Column() {
                    settingsList.forEach { item ->
                        when (item) {
                            is SettingUITypes.Toggle -> {
                                ListItem(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    headlineContent = {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = item.title,
                                            style = MaterialTheme.typography.bodyLarge,
                                        )
                                    },
                                    trailingContent = {
                                        Switch(
                                            checked = item.checked,
                                            onCheckedChange = item.onChecked
                                        )
                                    }
                                )
                            }
                        }
                    }

                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth(),
                        headlineContent = {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "参与开发&反馈",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        },
                        trailingContent = { ConnectionRow(context) }
                    )
                }

            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                onClick = {
                    profileScreenViewModel.logout {
                        navController.navigate("login") {
                            popUpTo("pages") { inclusive = true }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text(
                    text = "退出登录",
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        }
    }
}

@Composable
private fun ConnectionRow(context: Context) {
    Row() {
        IconButton(
            onClick = {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "https://github.com/pythagodzi11a/Courser.git".toUri()
                    )
                )
            }) {

            Icon(
                imageVector = FontAwesomeIcons.Brands.Github,
                contentDescription = "GitHub",
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(onClick = {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("QQ", "1098721138"))
            Toast.makeText(context, "QQ号已复制到剪贴板", Toast.LENGTH_SHORT).show()
        }) {
            Icon(
                imageVector = FontAwesomeIcons.Brands.Qq,
                contentDescription = "QQ",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}