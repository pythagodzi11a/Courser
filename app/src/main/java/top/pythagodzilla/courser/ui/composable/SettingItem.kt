package top.pythagodzilla.courser.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SettingItem() {
    ListItem(
        modifier = Modifier.clickable(onClick = {

        }),
        leadingContent = {
            Icon(
                imageVector = Icons.Default.AcUnit,
                contentDescription = ""
            )
        },
        headlineContent = { Text("乐") },
        supportingContent = { Text("Pythagodzilla") },
        trailingContent = { Text("修改") },
    )
}

@Preview
@Composable
fun SettingItemPreview() {
    SettingItem()
}