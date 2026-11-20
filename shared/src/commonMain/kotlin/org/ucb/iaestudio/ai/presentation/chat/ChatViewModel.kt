package org.ucb.iaestudio.ai.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ucb.iaestudio.ai.domain.model.ChatMessage
import org.ucb.iaestudio.ai.domain.usecase.ChatWithAssistantUseCase

// El chat habla con el agente de Koog, que puede usar herramientas de la app (ver StudyAgentService)
class ChatViewModel(
    val chatWithAssistantUseCase: ChatWithAssistantUseCase
) : ViewModel() {

    private val _effect = MutableSharedFlow<ChatEffect>()
    val effect = _effect.asSharedFlow()

    private val _state = MutableStateFlow(ChatUiState())
    val state = _state.asStateFlow()

    private var sendJob: Job? = null

    fun emitEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.OnInputChanged -> _state.update { it.copy(input = event.value) }
            ChatEvent.OnSend -> send()
            ChatEvent.OnNewChat -> newChat()
            is ChatEvent.OnSuggestion -> {
                _state.update { it.copy(input = event.text) }
                send()
            }
        }
    }

    // Borra el historial; si había una respuesta en camino, se cancela para que no aparezca
    private fun newChat() {
        sendJob?.cancel()
        _state.value = ChatUiState()
    }

    private fun emitEffect(effect: ChatEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    private fun send() {
        val text = _state.value.input.trim()
        if (text.isBlank()) {
            emitEffect(ChatEffect.ShowToast("Escribe un mensaje"))
            return
        }
        // La conversación completa se guarda aquí y se envía en cada pregunta
        val history = _state.value.messages + ChatMessage(text = text, isUser = true)
        _state.update {
            it.copy(messages = history, input = "", isLoading = true, error = null)
        }
        sendJob = viewModelScope.launch {
            val result = chatWithAssistantUseCase(history)

            result.onSuccess { reply ->
                val answer = ChatMessage(text = reply.text, isUser = false, toolsUsed = reply.toolsUsed)
                _state.update {
                    it.copy(messages = it.messages + answer, isLoading = false)
                }
            }
            result.onFailure { e ->
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Error desconocido")
                }
            }
        }
    }
}
