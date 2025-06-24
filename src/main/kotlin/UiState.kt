data class UiState(
    val darkMode: Boolean = false,
    val snippets: List<Snippet> = emptyList(),
    val linesOfVisibleCode: Int = 20
)