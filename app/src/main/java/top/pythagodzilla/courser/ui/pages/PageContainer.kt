package top.pythagodzilla.courser.ui.pages

import androidx.compose.animation.Crossfade
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

@Composable
fun PageContainer(navController: NavController) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.TASKS) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(it.icon, contentDescription = it.contentDescription)
                    },
                    label = { Text(it.label) },
                    selected = currentDestination == it,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Crossfade(targetState = currentDestination) { destination ->
            when (destination) {
                AppDestinations.TASKS -> TasksScreen()
                AppDestinations.PROFILE -> ProfileScreen(navController = navController)
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    TASKS("任务", Icons.Default.Task, "任务"),
    PROFILE("个人", Icons.Default.AccountCircle, "个人"),
}