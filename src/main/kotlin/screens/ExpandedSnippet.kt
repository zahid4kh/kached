package screens

import MainViewModel
import Snippet
import UiState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import deskit.dialogs.InfoDialog
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxThemes
import generateAnnotatedString
import kached.resources.Res
import kached.resources.copy
import kached.resources.square_m
import org.jetbrains.compose.resources.painterResource
import theme.getJetbrainsMonoFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedSnippet(
    uiState: UiState,
    viewModel: MainViewModel,
    openedSnippet: Snippet
){
    InfoDialog(
        width = 1200.dp,
        height = 1000.dp,
        title = openedSnippet.title,
        message = openedSnippet.description?:"",
        onClose = { viewModel.collapseSnippet() },
        content = {
            Column{
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End){
                    // copy icon buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            openedSnippet.language.toString().lowercase(),
                            fontFamily = getJetbrainsMonoFamily(),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)){
                            AnimatedVisibility(visible = uiState.codeCopiedAsText == true){
                                Box(modifier = Modifier
                                    .border(width = 1.dp, color = Color.Green.copy(green = 100f), shape = MaterialTheme.shapes.medium)){
                                    Text("Copied as text!", modifier = Modifier.padding(5.dp),
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            AnimatedVisibility(visible = uiState.codeCopiedAsText == false){
                                Box(modifier = Modifier
                                    .border(width = 1.dp, color = Color.Red.copy(red = 170f), shape = MaterialTheme.shapes.medium)){
                                    Text("Failed to copy as text!", modifier = Modifier.padding(5.dp),
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            // copy as plain-text
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                                tooltip = {
                                    RichTooltip(
                                        title = {
                                            Text(
                                                text = "Copy as text",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        },
                                        caretSize = DpSize(27.dp, 17.dp),
                                        colors = RichTooltipColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            actionContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    ) {
                                        Text("Code will be copied as plain text")
                                    }
                                },
                                state = rememberTooltipState()
                            ){
                                IconButton(onClick = {viewModel.copyCodeAsPlainText(openedSnippet)}){
                                    Icon(
                                        painterResource(Res.drawable.copy),
                                        contentDescription = "Copy code as plaintext"
                                    )
                                }
                            }

                            AnimatedVisibility(visible = uiState.codeCopiedAsMarkdown == true){
                                Box(modifier = Modifier
                                    .border(width = 1.dp, color = Color.Green.copy(green = 100f), shape = MaterialTheme.shapes.medium)){
                                    Text("Copied as markdown!", modifier = Modifier.padding(5.dp),
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            AnimatedVisibility(visible = uiState.codeCopiedAsText == false){
                                Box(modifier = Modifier
                                    .border(width = 1.dp, color = Color.Red.copy(red = 170f), shape = MaterialTheme.shapes.medium)){
                                    Text("Failed to copy as markdown!", modifier = Modifier.padding(5.dp),
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            // copy as markdown codeblock
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                                tooltip = {
                                    RichTooltip(
                                        title = {
                                            Text(
                                                text = "Copy as markdown",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        },
                                        caretSize = DpSize(27.dp, 17.dp),
                                        colors = RichTooltipColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            actionContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    ) {
                                        Text("Code will be copied as markdown codeblock")
                                    }
                                },
                                state = rememberTooltipState()
                            ){
                                IconButton(onClick = {viewModel.copyCodeAsMarkdown(openedSnippet)}){
                                    Icon(
                                        painterResource(Res.drawable.square_m),
                                        contentDescription = "Copy code as a markdown codeblock"
                                    )
                                }
                            }

                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f))
                        .padding(12.dp)
                ) {
                    val code = openedSnippet.code ?: "// No code here"
                    val highlights = remember(code) {
                        Highlights.Builder()
                            .code(code)
                            .language(SyntaxLanguage.KOTLIN) // will make this dynamic
                            .theme(SyntaxThemes.atom()) // will make this dynamic
                            .build()
                    }
                    ViewCodeText(highlights = highlights) // edited version of CodeTextView()
                }
            }
        }
    )
}



/*
copied from CodeTextView() from `package dev.snipme.kodeview.view`
had to add the `fontFamily = getJetbrainsMonoFamily()`
*/
@Composable
private fun ViewCodeText(
    modifier: Modifier = Modifier.background(Color.Transparent),
    highlights: Highlights
) {
    var textState by remember {
        mutableStateOf(AnnotatedString(highlights.getCode()))
    }

    LaunchedEffect(highlights) {
        textState = highlights
            .getHighlights()
            .generateAnnotatedString(highlights.getCode())
    }

    Surface(
        modifier = modifier,
        color = Color.Transparent
    ) {
        Text(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState()),
            text = textState,
            fontFamily = getJetbrainsMonoFamily()
        )
    }
}