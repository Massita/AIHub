package com.massita.aihub.ui.chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.massita.aihub.data.model.AiProvider
import com.massita.aihub.ui.theme.AiHubTheme

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateToConfiguration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChatScreenContent(
        uiState = uiState,
        onInputChanged = viewModel::onInputChanged,
        onSendMessage = viewModel::onSendMessage,
        onProviderSelected = viewModel::onProviderSelected,
        onToggleModelSelector = viewModel::onToggleModelSelector,
        onDismissModelSelector = viewModel::onDismissModelSelector,
        onNavigateToConfiguration = onNavigateToConfiguration,
        modifier = modifier
    )
}

@Composable
private fun ChatScreenContent(
    uiState: ChatUiState,
    onInputChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onProviderSelected: (AiProvider) -> Unit,
    onToggleModelSelector: () -> Unit,
    onDismissModelSelector: () -> Unit,
    onNavigateToConfiguration: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!uiState.hasConfiguredModels) {
        NoModelsEmptyState(
            onNavigateToConfiguration = onNavigateToConfiguration,
            modifier = modifier
        )
    } else {
        Column(modifier = modifier.fillMaxSize().imePadding()) {
            ModelSelectorBar(
                selectedProvider = uiState.selectedProvider,
                configuredProviders = uiState.configuredProviders,
                isExpanded = uiState.isModelSelectorExpanded,
                onToggle = onToggleModelSelector,
                onDismiss = onDismissModelSelector,
                onSelect = onProviderSelected
            )

            val listState = rememberLazyListState()

            LaunchedEffect(uiState.messages.size) {
                if (uiState.messages.isNotEmpty()) {
                    listState.animateScrollToItem(uiState.messages.lastIndex)
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.messages.isEmpty()) {
                    item {
                        ChatWelcome(providerName = uiState.selectedProvider?.displayName.orEmpty())
                    }
                }

                items(uiState.messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }
            }

            ChatInputBar(
                text = uiState.inputText,
                onTextChanged = onInputChanged,
                onSend = onSendMessage,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// region Empty State

@Composable
private fun NoModelsEmptyState(
    onNavigateToConfiguration: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .padding(32.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f)
                            )
                        )
                    )
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Psychology,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "No models configured",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Connect an AI provider to start chatting. " +
                            "You can configure API keys for OpenAI, Google Gemini, " +
                            "or Alibaba Qwen in the settings.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(onClick = onNavigateToConfiguration) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Go to Configuration")
                }
            }
        }
    }
}

// endregion

// region Model Selector

@Composable
private fun ModelSelectorBar(
    selectedProvider: AiProvider?,
    configuredProviders: List<AiProvider>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onDismiss: () -> Unit,
    onSelect: (AiProvider) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            TextButton(onClick = onToggle) {
                Text(
                    text = selectedProvider?.displayName ?: "Select model",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = "Change model"
                )
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = onDismiss
            ) {
                configuredProviders.forEach { provider ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = provider.displayName,
                                fontWeight = if (provider == selectedProvider) FontWeight.Bold
                                else FontWeight.Normal
                            )
                        },
                        onClick = { onSelect(provider) }
                    )
                }
            }
        }
    }
}

// endregion

// region Chat Messages

@Composable
private fun ChatWelcome(
    providerName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Psychology,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Start a conversation",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )

        Text(
            text = "Type a message below to chat with $providerName.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
        )
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val backgroundColor = if (message.isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (message.isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val shape = if (message.isUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .animateContentSize(),
            shape = shape,
            color = backgroundColor,
            tonalElevation = if (message.isUser) 0.dp else 1.dp
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
        }
    }
}

// endregion

// region Chat Input

@Composable
private fun ChatInputBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message…") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                maxLines = 4
            )

            FilledIconButton(
                onClick = onSend,
                enabled = text.isNotBlank(),
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message"
                )
            }
        }
    }
}

// endregion

// region Previews

@Preview(showBackground = true)
@Composable
private fun ChatScreenEmptyStatePreview() {
    AiHubTheme {
        NoModelsEmptyState(onNavigateToConfiguration = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenWithMessagesPreview() {
    val state = ChatUiState(
        messages = listOf(
            ChatMessage(content = "Hello! Can you help me with Kotlin?", isUser = true),
            ChatMessage(
                content = "Of course! I'd be happy to help with Kotlin. What would you like to know?",
                isUser = false
            ),
            ChatMessage(content = "How do I use coroutines?", isUser = true)
        ),
        selectedProvider = AiProvider.OPENAI,
        configuredProviders = listOf(AiProvider.OPENAI, AiProvider.GEMINI)
    )

    AiHubTheme {
        Surface {
            ChatScreenContent(
                uiState = state,
                onInputChanged = {},
                onSendMessage = {},
                onProviderSelected = {},
                onToggleModelSelector = {},
                onDismissModelSelector = {},
                onNavigateToConfiguration = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenWelcomePreview() {
    val state = ChatUiState(
        selectedProvider = AiProvider.GEMINI,
        configuredProviders = listOf(AiProvider.GEMINI)
    )

    AiHubTheme {
        Surface {
            ChatScreenContent(
                uiState = state,
                onInputChanged = {},
                onSendMessage = {},
                onProviderSelected = {},
                onToggleModelSelector = {},
                onDismissModelSelector = {},
                onNavigateToConfiguration = {}
            )
        }
    }
}

// endregion
