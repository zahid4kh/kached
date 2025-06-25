data class UiState(
    val darkMode: Boolean = false,
    val snippets: List<Snippet> = emptyList(),
    val linesOfVisibleCode: Int = 20,
    val expandedSnippet: Snippet? = null,
    val codeCopiedAsText: Boolean? = null,
    val codeCopiedAsMarkdown: Boolean? = null
)