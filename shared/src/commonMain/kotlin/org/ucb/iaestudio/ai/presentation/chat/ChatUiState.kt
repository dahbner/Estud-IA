package org.ucb.iaestudio.ai.presentation.chat

import org.ucb.iaestudio.ai.domain.model.ChatMessage

data class ChatUiState(
    val input: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
