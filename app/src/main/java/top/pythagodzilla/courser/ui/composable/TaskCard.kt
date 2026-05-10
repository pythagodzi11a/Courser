package top.pythagodzilla.courser.ui.composable

import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.pythagodzilla.courser.ui.types.ExamUIClass
import top.pythagodzilla.courser.ui.types.HomeworkUIClass
import top.pythagodzilla.courser.ui.types.TaskUITypes

@Composable
fun TaskCard(task: TaskUITypes) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(),
        onClick = {}
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
                color = when(task){
                    is HomeworkUIClass -> MaterialTheme.colorScheme.primary
                    is ExamUIClass -> MaterialTheme.colorScheme.secondary
                }
            ) {
                Text(
                    text = when(task){
                        is HomeworkUIClass -> "作业"
                        is ExamUIClass -> "考试"
                    },
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

//        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
//            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Text(text = "开始时间："+task.endTime)
            }
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
