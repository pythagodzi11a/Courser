package top.pythagodzilla.courser.ui.types

import androidx.compose.ui.graphics.vector.ImageVector

data class SettingBlockData(
    val settingTitle: String,
    val items: List<SettingUITypes>
)

sealed class SettingUITypes {
    data class Toggle(
        val title: String,
        val icon: ImageVector,
        val contentDescription: String,
        val checked: Boolean,
        val onChecked: (Boolean) -> Unit
    ) : SettingUITypes()

    data class JumpPage(
        val title: String,
        val icon: ImageVector,
        val contentDescription: String,
        val onClick: () -> Unit,
    ) : SettingUITypes()
}