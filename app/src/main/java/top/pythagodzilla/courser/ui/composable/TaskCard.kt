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
import androidx.compose.ui.unit.sp
import top.pythagodzilla.courser.ui.types.ExamUIClass
import top.pythagodzilla.courser.ui.types.HomeworkUIClass
import top.pythagodzilla.courser.ui.types.TaskUITypes

@Composable
fun TaskCard(task: TaskUITypes) {

    when (task) {
        is HomeworkUIClass -> {
            HomeworkCard(task)
        }

        is ExamUIClass -> {
            ExamCard(task)
        }
    }

}

@Composable
private fun ExamCard(task: ExamUIClass) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(),
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task.courseName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = task.endTime,
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
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Text(text = task.startTime)
        }
    }
}

@Composable
private fun HomeworkCard(task: HomeworkUIClass) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(),
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task.courseName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = task.endTime,
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
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 30.sp
            )
            Text(text = task.startTime)
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
    Column() {
        TaskCard(
            HomeworkUIClass(

                title = "Task Title",
                startTime = "2024-06-01T08:00:00",
                endTime = "2024-06-07T23:59:59",
                courseName = "Course Name"
            )
        )
        Spacer(modifier = Modifier.padding(4.dp))
        TaskCard(
            ExamUIClass(
                title = "Exam Title",
                startTime = "2024-06-01T08:00:00",
                endTime = "2024-06-07T23:59:59",
                courseName = "Course Name"
            )
        )
    }
}
