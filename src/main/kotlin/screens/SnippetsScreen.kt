
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import deskit.dialogs.file.filesaver.FileSaverDialog
import moe.tlaster.precompose.navigation.Navigator
import screens.ExpandedSnippet

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
                        onExportAsText = {
                            viewModel.showExportAsTextDialog(snippet)
                        },
                        onExportAsMarkdown = {
                            viewModel.showExportAsMdDialog(snippet)
                        },
                        onExportAsLanguage = {
                            viewModel.showExportAsLanguageDialog(snippet)
                        }
                    )
                }
            }
            uiState.expandedSnippet?.let{openedSnippet->
                ExpandedSnippet(
                    uiState = uiState,
                    viewModel = viewModel,
                    openedSnippet = openedSnippet
                )
            }

            if(uiState.showSaveAsTextDialog && uiState.selectedSnippetToSave != null){
                FileSaverDialog(
                    title = "Save as a text file",
                    extension = ".txt",
                    suggestedFileName = uiState.selectedSnippetToSave?.title!!,
                    onSave = {file->
                        viewModel.saveAsPlainText(
                            snippet = uiState.selectedSnippetToSave?:Snippet(),
                            file = file
                        )
                    },
                    onCancel = {viewModel.closeExportAsTextDialog()}
                )
            }

            if(uiState.showSaveAsMdDialog && uiState.selectedSnippetToSave != null){
                FileSaverDialog(
                    title = "Save as a markdown file",
                    extension = ".md",
                    suggestedFileName = uiState.selectedSnippetToSave?.title!!,
                    onSave = {file->
                        viewModel.saveAsMarkdown(
                            snippet = uiState.selectedSnippetToSave?:Snippet(),
                            file = file
                        )
                    },
                    onCancel = {viewModel.closeExportAsMdDialog()}
                )
            }

            if(uiState.showSaveAsLanguageDialog && uiState.selectedSnippetToSave != null){
                FileSaverDialog(
                    title = "Save as ${uiState.selectedSnippetToSave?.language?.getFileExtension() ?: ".txt"} file",
                    extension = uiState.selectedSnippetToSave?.language?.getFileExtension() ?: ".txt",
                    suggestedFileName = uiState.selectedSnippetToSave?.title!!,
                    onSave = {file->
                        viewModel.saveAsLanguageFile(
                            snippet = uiState.selectedSnippetToSave?:Snippet(),
                            file = file
                        )
                    },
                    onCancel = {viewModel.closeExportAsLanguageDialog()}
                )
            }
        }
    }
}