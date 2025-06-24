import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kached.resources.Res
import kached.resources.move_left
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.painterResource
import theme.getJetbrainsMonoFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSnippetScreen(navigator: Navigator, viewModel: MainViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    val jbMonoFamily = getJetbrainsMonoFamily()

    val focusManager = LocalFocusManager.current

    val textFieldShape = MaterialTheme.shapes.medium

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add New Snippet",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            painter = painterResource(Res.drawable.move_left),
                            contentDescription = "Back",
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
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // SNIPPET TITLE
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .onPreviewKeyEvent {
                        if (it.type == KeyEventType.KeyUp && it.key == Key.Tab) {
                            focusManager.moveFocus(FocusDirection.Down)
                            true
                        } else {
                            false
                        }
                    },
                shape = textFieldShape
            )
            // SNIPPET DESCRIPTION
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .heightIn(min = 80.dp)
                    .animateContentSize()
                    .onPreviewKeyEvent {
                        if (it.type == KeyEventType.KeyUp && it.key == Key.Tab) {
                            focusManager.moveFocus(FocusDirection.Down)
                            true
                        } else {
                            false
                        }
                    },
                maxLines = 5,
                shape = textFieldShape
            )
            // SNIPPET
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                textStyle = TextStyle(fontFamily = jbMonoFamily),
                label = { Text("Code") },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .heightIn(min = 150.dp)
                    .animateContentSize(),
                maxLines = 10,
                shape = textFieldShape
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navigator.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onErrorContainer)
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val newSnippet = Snippet(
                                title = title,
                                description = description.ifBlank { null },
                                code = code.ifBlank { null }
                            )
                            viewModel.addSnippet(newSnippet)
                            navigator.navigate("/snippets")
                        } else {
                            // TODO: Show Info Dialog!!!
                            println("Title cannot be empty!")
                        }
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("Save Snippet", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}