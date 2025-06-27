
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kached.resources.*
import org.jetbrains.compose.resources.painterResource
import theme.getJetbrainsMonoFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetCard(
    snippet: Snippet,
    onDelete: (String) -> Unit,
    modifier: Modifier,
    onMaximizeSnippet: () -> Unit = {},
    onExportAsText: () -> Unit = {},
    onExportAsMarkdown: () -> Unit = {},
    onExportAsLanguage: () -> Unit = {}
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    snippet.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Box{
                    TooltipBox(
                        tooltip = {PlainTooltip { Text("Export as") }},
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        state = rememberTooltipState()
                    ){
                        IconButton(
                            onClick = {dropdownExpanded = !dropdownExpanded}
                        ){
                            Icon(
                                painter = painterResource(Res.drawable.download),
                                contentDescription = "Export as"
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = {dropdownExpanded = false},
                        shape = MaterialTheme.shapes.medium,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ){
                        DropdownMenuItem(
                            text = {Text("Export as .txt")},
                            leadingIcon = {
                                Icon(
                                    painterResource(Res.drawable.text),
                                    contentDescription = "Export as .txt",
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {onExportAsText()}
                        )
                        DropdownMenuItem(
                            text = {Text("Export as .md")},
                            leadingIcon = {
                                Icon(
                                    painterResource(Res.drawable.markdown),
                                    contentDescription = "Export as .md",
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {onExportAsMarkdown()}
                        )

                        if (snippet.language != null && snippet.language != Languages.NONE) {
                            DropdownMenuItem(
                                text = {Text("Export as ${snippet.language.getFileExtension()}")},
                                leadingIcon = {
                                    Icon(
                                        painterResource(Res.drawable.braces),
                                        contentDescription = "Export as ${snippet.language.getFileExtension()}",
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {onExportAsLanguage()}
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        onMaximizeSnippet()
                    },
                ) {
                    Icon(painter = painterResource(Res.drawable.maximize),
                        contentDescription = "Show more",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(
                    onClick = { onDelete(snippet.title) },
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Snippet", tint = MaterialTheme.colorScheme.error)
                }
            }


            Spacer(Modifier.height(8.dp))


            snippet.description?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
            }


            snippet.code?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    fontFamily = getJetbrainsMonoFamily(),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
                        .padding(8.dp),
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
