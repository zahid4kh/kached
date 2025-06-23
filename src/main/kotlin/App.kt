import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import theme.AppTheme


@Composable
@Preview
fun App(
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    AppTheme(darkTheme = uiState.darkMode) {
        val secondSnippet = Snippet(
            title = "Second title",
            description = "kached app sfvhndfvjdfv",
            code = "hello world"
        )
        val firstSnippet = Snippet(
            title = "First title",
            description = "dfvbjdfnvsdmfkvknnb",
            code = "fjvnjdfvnjmdfkvfvdv"
        )


        viewModel.removeSnippet(secondSnippet.title)
        viewModel.removeSnippet(firstSnippet.title)
    }
}

