package top.pythagodzilla.courser.ui.pages

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import top.pythagodzilla.courser.ui.composable.TaskCard
import top.pythagodzilla.courser.ui.viewModels.HomeScreenViewModel


@Composable
fun HomeScreen(
    homeViewModel: HomeScreenViewModel = viewModel()
) {
    var testByte by rememberSaveable { mutableStateOf("") }
    val tasksUIList by homeViewModel.tasksUIList.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(tasksUIList.size) {
                TaskCard(tasksUIList[it])
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }


}
