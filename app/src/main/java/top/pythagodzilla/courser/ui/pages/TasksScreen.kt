package top.pythagodzilla.courser.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import top.pythagodzilla.courser.ui.composable.TaskCard
import top.pythagodzilla.courser.ui.viewModels.TasksScreenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    tasksViewModel: TasksScreenViewModel = viewModel()
) {
    val listState = rememberLazyListState()
    val tasksUIList by tasksViewModel.tasksUIList.collectAsStateWithLifecycle()
    val sortMode by tasksViewModel.sortMode.collectAsStateWithLifecycle()
    var sortMenuExpended by remember { mutableStateOf(false) }

    LaunchedEffect(sortMode) {
        listState.animateScrollToItem(0)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        text = "Courser",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 30.sp
                    )
                },
                actions = {
                    SortMenu(sortMenuExpended, tasksViewModel) { sortMenuExpended = it }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = listState
        ) {
            items(tasksUIList.size, key = { "${tasksUIList[it].courseId}_${tasksUIList[it].id}" }) {
                Column(modifier = Modifier.animateItem()) {
                    TaskCard(tasksUIList[it], tasksViewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }


}

@Composable
private fun SortMenu(
    sortMenuExpended: Boolean,
    tasksViewModel: TasksScreenViewModel,
    onExpendedChange: (Boolean) -> Unit = {}
) {
    Box(modifier = Modifier.padding(8.dp)) {
        FilterChip(
            onClick = { onExpendedChange(!sortMenuExpended) },
            selected = sortMenuExpended,
            label = { Text("排序") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.FormatLineSpacing,
                    contentDescription = "Sort"
                )
            }
        )

        DropdownMenu(
            expanded = sortMenuExpended,
            onDismissRequest = { onExpendedChange(false) }) {
            DropdownMenuItem(
                text = { Text("结束时间") },
                onClick = {
                    tasksViewModel.setSortMode(TasksScreenViewModel.SortMode.BY_END_TIME)
                })

            DropdownMenuItem(
                text = { Text("课程名称") },
                onClick = {
                    tasksViewModel.setSortMode(TasksScreenViewModel.SortMode.BY_COURSE_NAME)
                })

            DropdownMenuItem(
                text = { Text("开始时间") },
                onClick = {
                    tasksViewModel.setSortMode(TasksScreenViewModel.SortMode.BY_START_TIME)
                })
        }
    }
}
