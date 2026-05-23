package top.pythagodzilla.courser.ui.composable

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.pythagodzilla.courser.network.response.HomeworkViewResponse
import top.pythagodzilla.courser.ui.types.ExamClass
import top.pythagodzilla.courser.ui.types.HomeworkClass
import top.pythagodzilla.courser.ui.types.TasksType
import top.pythagodzilla.courser.ui.viewModels.TasksScreenViewModel

@Composable
fun TaskCard(task: TasksType, tasksScreenViewModel: TasksScreenViewModel) {
    var expend by remember { mutableStateOf(false) }
    var taskDetail by remember { mutableStateOf<HomeworkViewResponse?>(null) }

    LaunchedEffect(task.courseId, task.id) {
        taskDetail = null
        if (task is HomeworkClass) {
            taskDetail = withContext(Dispatchers.IO) {
                tasksScreenViewModel.getTasksDetails(task.courseId, task.id)
            }
        }
    }

    Card(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth()
            .padding(8.dp),
//        colors = CardDefaults.cardColors(),
        onClick = { expend = !expend },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.courseName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Surface(
                shape = RoundedCornerShape(corner = CornerSize(50)),
                color = when (task) {
                    is HomeworkClass -> MaterialTheme.colorScheme.primary
                    is ExamClass -> MaterialTheme.colorScheme.tertiary
                }
            ) {
                Text(
                    text = when (task) {
                        is HomeworkClass -> "作业"
                        is ExamClass -> "测试"
                    },
                    color = when (task) {
                        is HomeworkClass -> MaterialTheme.colorScheme.onPrimary
                        is ExamClass -> MaterialTheme.colorScheme.onTertiary
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Text(
                text = task.taskTitle,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "截止时间：" + task.endTime)
            }
        }

        ExpendCard(expend, taskDetail?.datas?.taskContent) { expend = false }
    }
}

@Composable
private fun ExpendCard(
    expend: Boolean,
    taskDetail: String?,
    onClose: () -> Unit = {}
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    AnimatedVisibility(
        visible = expend,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .clickable(onClick = { onClose() })
        ) {
            Text(
                text = "作业详情",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = false
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        webViewClient = WebViewClient()
                    }
                },
                update = { webView ->
                    val html = if (isDark) {
                        """<!DOCTYPE html><html><head><meta name="color-scheme" content="dark"><style>img{max-width:100%;height:auto}*{color:#E0E0E0!important;background-color:transparent!important}a{color:#81D4FA!important}</style></head><body>${taskDetail ?: "暂无详情"}</body></html>"""
                    } else {
                        """<!DOCTYPE html><html><head><meta name="color-scheme" content="light"><style>img{max-width:100%;height:auto}</style></head><body>${taskDetail ?: "暂无详情"}</body></html>"""
                    }
                    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onClose() }) {
                    Icon(imageVector = Icons.Default.ExpandLess, contentDescription = "收起")
                }
            }
        }
    }
}