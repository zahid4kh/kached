import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class MainViewModel(
    private val database: Database,
): ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _snippets = MutableStateFlow<List<Snippet>>(emptyList())
    val snippets: StateFlow<List<Snippet>> = _snippets.asStateFlow()


    private val scope = viewModelScope

    init {
        scope.launch(Dispatchers.IO) {
            val settings = database.getSettings()
            _uiState.value = _uiState.value.copy(
                darkMode = settings.darkMode,
            )
            val loadedSnippets = database.getSnippets()
            _snippets.value = loadedSnippets
        }
    }

    fun addSnippet(newSnippet: Snippet) {
        val newSnippets = _snippets.value + newSnippet
        _snippets.value = newSnippets

        scope.launch(Dispatchers.IO) {
            database.saveSnippets((newSnippets))
        }
    }

    fun removeSnippet(snippetTitleToRemove: String) {
        val updatedSnippets = _snippets.value.filter{it.title != snippetTitleToRemove}
        _snippets.value = updatedSnippets
        scope.launch(Dispatchers.IO){
            database.saveSnippets(updatedSnippets)
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