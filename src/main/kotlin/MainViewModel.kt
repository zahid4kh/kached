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
import java.io.File

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

//    fun setSnippetLanguage(snippetToUpdate: Snippet, language: Languages){
//        _uiState.update { currentState->
//            val updated = currentState.snippets.map { snippet->
//                if(snippet.title == snippetToUpdate.title){
//                    snippet.copy(
//                        language = language
//                    )
//                }else{
//                    snippet
//                }
//            }
//            currentState.copy(
//                snippets = updated
//            )
//        }
//    }

    fun selectLanguage(language: Languages){
        _uiState.update {it.copy(selectedLanguage = language)}
    }

    fun expandSnippet(snippet: Snippet){
        _uiState.update { it.copy(expandedSnippet = snippet) }
    }

    fun collapseSnippet(){
        _uiState.update { it.copy(expandedSnippet = null) }
    }

    fun showExportAsTextDialog(snippet: Snippet){
        _uiState.update {
            it.copy(
                showSaveAsTextDialog = true,
                selectedSnippetToSave = snippet
            )
        }
    }

    fun closeExportAsTextDialog(){
        _uiState.update {
            it.copy(
                showSaveAsTextDialog = false,
                selectedSnippetToSave = null
            )
        }
    }

    fun showExportAsMdDialog(snippet: Snippet){
        _uiState.update {
            it.copy(
                showSaveAsMdDialog = true,
                selectedSnippetToSave = snippet
            )
        }
    }

    fun closeExportAsMdDialog(){
        _uiState.update {
            it.copy(
                showSaveAsMdDialog = false,
                selectedSnippetToSave = null
            )
        }
    }

    fun saveAsPlainText(snippet: Snippet, file: File){
        scope.launch(Dispatchers.IO) {
            val content = snippet.code?:"No code was found, therefore none written!"
            file.writeText(content)
            delay(100)
            closeExportAsTextDialog()
        }
    }

    fun saveAsMarkdown(snippet: Snippet, file: File){
        val language = if(snippet.language == Languages.NONE){
            ""
        }else{
            snippet.language.toString().lowercase()
        }
        val content = """
## ${snippet.title}

### ${snippet.description?:""}

```$language
${snippet.code?:"No code was found, therefore none written!"}
```
        """.trimIndent()
        scope.launch {
            file.writeText(content)
            delay(100)
            closeExportAsMdDialog()
        }
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
            _uiState.update { it.copy(codeCopiedAsText = true) }
        }catch (e: IllegalStateException){
            _uiState.update { it.copy(codeCopiedAsText = false) }
            e.printStackTrace()
        }
        scope.launch {
            delay(1200)
            _uiState.update { it.copy(codeCopiedAsText = null)}
        }
    }

    fun copyCodeAsMarkdown(snippet: Snippet){
        val language = if(snippet.language == Languages.NONE){
            ""
        }else{
            snippet.language.toString().lowercase()
        }
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        try {
            val codeToCopy = snippet.code?:"Could not find code to copy for snippet with title '${snippet.title}'"
            val finalMarkdownContent = """
```$language
$codeToCopy
```
            """.trimIndent()
            clipboard.setContents(StringSelection(finalMarkdownContent), null)
            _uiState.update { it.copy(codeCopiedAsMarkdown = true) }
        }catch (e: IllegalStateException){
            _uiState.update { it.copy(codeCopiedAsMarkdown = false) }
            e.printStackTrace()
        }
        scope.launch {
            delay(1200)
            _uiState.update { it.copy(codeCopiedAsMarkdown = null)}
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