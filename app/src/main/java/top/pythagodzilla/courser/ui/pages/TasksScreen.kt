package top.pythagodzilla.courser.ui.pages

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import top.pythagodzilla.courser.ui.composable.TaskCard
import top.pythagodzilla.courser.ui.viewModels.TasksScreenViewModel


@Composable
fun TasksScreen(
    tasksViewModel: TasksScreenViewModel = viewModel()
) {
    val tasksUIList by tasksViewModel.tasksUIList.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Text(
                text = "Courser",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 30.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(tasksUIList.size, key = { tasksUIList[it].taskTitle }) {
                TaskCard(tasksUIList[it],tasksViewModel)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }


}
