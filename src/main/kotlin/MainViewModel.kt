import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

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

    fun removeSnippet(snippetTitleToRemove: String) {
        val updatedSnippets = _uiState.value.snippets.filter { it.title != snippetTitleToRemove }
        _uiState.value = _uiState.value.copy(snippets = updatedSnippets)

        scope.launch {
            withContext(Dispatchers.IO) {
                database.saveSnippets(updatedSnippets)
            }
        }
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