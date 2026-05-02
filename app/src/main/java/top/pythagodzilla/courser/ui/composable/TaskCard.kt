package top.pythagodzilla.courser.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import top.pythagodzilla.courser.network.response.HomeworkClass
import top.pythagodzilla.courser.network.response.TaskItem
import java.time.LocalDateTime

@Composable
fun TaskCard(task: TaskItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = task.endTime.toString(),
                style = MaterialTheme.typography.titleSmall
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = task.startTime.toString())
            Text(text = task.publishStatus.toString())
        }

    }
}


@Preview(
    widthDp = 411,
    showBackground = true,
    backgroundColor = 0xFFFFEEEE
)
@Composable
fun TaskCardPreview() {
    TaskCard(
        HomeworkClass(
            id = 1,
            title = "Task Title",
            startTime = LocalDateTime.parse("2024-06-01T08:00:00"),
            endTime = LocalDateTime.parse("2024-06-07T23:59:59"),
            publishStatus = true,
            revision = true,
            fullmark = 1,
            lessId = 114514,
        )
    )
}
