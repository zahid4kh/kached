import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import deskit.dialogs.InfoDialog
import kached.resources.Res
import kached.resources.copy
import kached.resources.square_m
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.painterResource
import theme.getJetbrainsMonoFamily

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SnippetsScreen(navigator: Navigator, viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snippets = uiState.snippets

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Snippets (${snippets.size})") },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (snippets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No snippets found. Add some!", style = MaterialTheme.typography.headlineSmall)
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(20.dp)
                    .padding(paddingValues),
                columns = GridCells.Adaptive(400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ){
                items(
                    items = snippets,
                    key = {it}
                ){snippet ->
                    SnippetCard(
                        snippet = snippet,
                        onDelete = {
                            viewModel.removeSnippet(it)
                        },
                        modifier = Modifier.animateItem(placementSpec = spring()),
                        onMaximizeSnippet = {
                            viewModel.expandSnippet(snippet)
                        },
                    )
                }
            }
            uiState.expandedSnippet?.let{openedSnippet->
                InfoDialog(
                    width = 1200.dp,
                    height = 1000.dp,
                    title = openedSnippet.title,
                    message = openedSnippet.description?:"",
                    onClose = { viewModel.collapseSnippet() },
                    content = {
                        Column{
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.End
                            ){
                                // copy icon buttons
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)){
                                    // copy as plain-text
                                    TooltipBox(
                                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                                        tooltip = {
                                            RichTooltip(
                                                title = {
                                                    Text(
                                                        text = "Copy as text",
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )
                                                },
                                                caretSize = DpSize(27.dp, 17.dp),
                                                colors = RichTooltipColors(
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    actionContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                                )
                                            ) {
                                                Text("Code will be copied as plain text")
                                            }
                                        },
                                        state = rememberTooltipState()
                                    ){
                                        IconButton(
                                            onClick = {viewModel.copyCodeAsPlainText(openedSnippet)}
                                        ){
                                            Icon(
                                                painterResource(Res.drawable.copy),
                                                contentDescription = "Copy code as plaintext"
                                            )
                                        }
                                    }

                                    TooltipBox(
                                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                                        tooltip = {
                                            RichTooltip(
                                                title = {
                                                    Text(
                                                        text = "Copy as markdown",
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )
                                                },
                                                caretSize = DpSize(27.dp, 17.dp),
                                                colors = RichTooltipColors(
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    actionContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                                )
                                            ) {
                                                Text("Code will be copied as markdown codeblock")
                                            }
                                        },
                                        state = rememberTooltipState()
                                    ){
                                        // copy as markdown codeblock
                                        IconButton(
                                            onClick = {viewModel.copyCodeAsMarkdown(openedSnippet)}
                                        ){
                                            Icon(
                                                painterResource(Res.drawable.square_m),
                                                contentDescription = "Copy code as a markdown codeblock"
                                            )
                                        }
                                    }

                                }
                            }
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f))
                            ){
                                SelectionContainer {
                                    Text(
                                        text = openedSnippet.code?:"// No code here",
                                        fontFamily = getJetbrainsMonoFamily(),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }

                    }
                )
            }
        }
    }
}