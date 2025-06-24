import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class MainViewModel(
    private val database: Database,
): ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val scope = viewModelScope

    init {
        scope.launch(Dispatchers.IO) {
            val settings = database.getSettings()
            val loadedSnippets = database.getSnippets()

            _uiState.value = _uiState.value.copy(
                darkMode = settings.darkMode,
                snippets = loadedSnippets
            )
        }
    }

    fun addSnippet(newSnippet: Snippet) {
        val newSnippets = _uiState.value.snippets + newSnippet
        _uiState.value = _uiState.value.copy(snippets = newSnippets)

        scope.launch(Dispatchers.IO) {
            database.saveSnippets((newSnippets))
        }
    }

    fun expandSnippet(snippet: Snippet){
        _uiState.update { it.copy(expandedSnippet = snippet) }
    }

    fun collapseSnippet(){
        _uiState.update { it.copy(expandedSnippet = null) }
    }

    fun removeSnippet(snippetTitleToRemove: String) {
        val updatedSnippets = _uiState.value.snippets.filter { it.title != snippetTitleToRemove }
        _uiState.value = _uiState.value.copy(snippets = updatedSnippets)

        scope.launch {
            withContext(Dispatchers.IO) {
                database.saveSnippets(updatedSnippets)
            }
        }
    }

    fun copyCodeAsPlainText(snippet: Snippet){
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        try {
            val codeToCopy = snippet.code?:"Could not find code to copy for snippet with title '${snippet.title}'"
            clipboard.setContents(StringSelection(codeToCopy), null)
            _uiState.update { it.copy(codeCopied = true) }
        }catch (e: IllegalStateException){
            _uiState.update { it.copy(codeCopied = false) }
            e.printStackTrace()
        }
        scope.launch {
            delay(1200)
            _uiState.update { it.copy(codeCopied = null)}
        }
    }

    fun copyCodeAsMarkdown(snippet: Snippet){
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        try {
            val codeToCopy = snippet.code?:"Could not find code to copy for snippet with title '${snippet.title}'"
            val finalMarkdownContent = """
```kotlin
$codeToCopy
```
            """.trimIndent()
            clipboard.setContents(StringSelection(finalMarkdownContent), null)
            _uiState.update { it.copy(codeCopied = true) }
        }catch (e: IllegalStateException){
            _uiState.update { it.copy(codeCopied = false) }
            e.printStackTrace()
        }
        scope.launch {
            delay(1200)
            _uiState.update { it.copy(codeCopied = null)}
        }
    }

    fun expandCodeField(){
        _uiState.value = _uiState.value.copy(
            linesOfVisibleCode = 55
        )
    }

    fun collapseCodeField(){
        _uiState.value = _uiState.value.copy(
            linesOfVisibleCode = 20
        )
    }

    fun toggleDarkMode() {
        val newDarkMode = !_uiState.value.darkMode
        _uiState.value = _uiState.value.copy(darkMode = newDarkMode)

        scope.launch(Dispatchers.IO) {
            val settings = database.getSettings()
            database.saveSettings(settings.copy(darkMode = newDarkMode))
        }
    }

}