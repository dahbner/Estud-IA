package org.ucb.iaestudio.ai.presentation.chat

sealed interface ChatEvent {
    data class OnInputChanged(val value: String) : ChatEvent
    data object OnSend : ChatEvent
    data object OnNewChat : ChatEvent
    data class OnSuggestion(val text: String) : ChatEvent
}
