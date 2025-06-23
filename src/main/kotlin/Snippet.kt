import kotlinx.serialization.Serializable

@Serializable
data class Snippet(
    val title: String = "",
    val description: String? = null,
    val code: String? = null
)
