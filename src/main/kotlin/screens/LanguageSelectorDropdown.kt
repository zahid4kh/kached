package screens

import Languages
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LanguageSelectorDropdown(
    modifier: Modifier,
    isExpanded: Boolean = false,
    onDismiss: () -> Unit = {},
    onLanguageSelected: (Languages) -> Unit = {}
){
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = {onDismiss()},
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.height(320.dp),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
    ){
        val languages = Languages.entries.toList()
        languages.forEach { language ->
            DropdownMenuItem(
                text = {Text(language.name.lowercase())},
                onClick = {onLanguageSelected(language)}
            )
        }
    }
}