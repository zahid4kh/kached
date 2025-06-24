import androidx.compose.animation.animateBounds
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.unit.dp
import kached.resources.Res
import kached.resources.move_left
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSnippetScreen(navigator: Navigator, viewModel: MainViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

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
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
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