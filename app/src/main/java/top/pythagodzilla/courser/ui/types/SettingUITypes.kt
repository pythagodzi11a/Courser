package top.pythagodzilla.courser.ui.types

import androidx.compose.ui.graphics.vector.ImageVector

sealed class SettingUITypes {
    data class Toggle(
        val title: String,
        val icon: ImageVector,
        val contentDescription: String,
        val checked: Boolean,
        val onChecked: (Boolean) -> Unit
    ) : SettingUITypes()
}