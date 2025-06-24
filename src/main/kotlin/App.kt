import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import theme.AppTheme


@Composable
@Preview
fun App(
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    PreComposeApp {
        AppTheme(darkTheme = uiState.darkMode) {
            val navigator = rememberNavigator()
            Surface {
                NavHost(navigator = navigator, initialRoute = "/main") {
                    scene("/main") {
                        MainScreen(navigator = navigator, viewModel = viewModel, uiState = uiState)
                    }
                    scene("/add_snippet") {
                        AddSnippetScreen(navigator = navigator, viewModel = viewModel)
                    }
                    scene("/snippets") {
                        SnippetsScreen(navigator = navigator, viewModel = viewModel)
                    }
                }
            }
        }
    }
}