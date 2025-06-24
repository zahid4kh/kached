import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kached.resources.Res
import kached.resources.ic_plus
import kached.resources.layout_list
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navigator: Navigator, viewModel: MainViewModel, uiState: UiState) {
    val isDark = uiState.darkMode

    val cardTitleStyle = MaterialTheme.typography.headlineSmall
    val cardIconSize = 30.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kached",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleDarkMode() }) {
                        Icon(
                            imageVector = if(isDark) Icons.Default.ModeNight else Icons.Default.LightMode,
                            contentDescription = "Toggle Dark Mode",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                modifier = Modifier.clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(120.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { navigator.navigate("/add_snippet") },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_plus),
                            contentDescription = "Add New Snippet",
                            modifier = Modifier.size(cardIconSize),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "New Snippet",
                            style = cardTitleStyle,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(120.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { navigator.navigate("/snippets") },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(Res.drawable.layout_list),
                            contentDescription = "View All Snippets",
                            modifier = Modifier.size(cardIconSize),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "View All Snippets",
                            style = cardTitleStyle,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}