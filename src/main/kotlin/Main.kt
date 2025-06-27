@file:JvmName("Kached")
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kached.resources.Res
import kached.resources.kached
import moe.tlaster.precompose.ProvidePreComposeLocals
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin
import theme.AppTheme
import java.awt.Dimension


fun main() = application {
    startKoin {
        modules(appModule)
    }

    val viewModel = getKoin().get<MainViewModel>()

    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(size = DpSize(800.dp, 600.dp)),
        alwaysOnTop = true,
        title = "Kached - Code Snippet Manager",
        icon = painterResource(Res.drawable.kached)
    ) {
        window.minimumSize = Dimension(800, 600)

        AppTheme {
            ProvidePreComposeLocals {
                App(
                    viewModel = viewModel
                )
            }

        }
    }
}