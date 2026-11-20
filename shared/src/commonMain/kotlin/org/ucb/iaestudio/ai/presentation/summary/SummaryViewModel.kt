package org.ucb.iaestudio.ai.presentation.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ucb.iaestudio.ai.domain.model.ChatMessage
import org.ucb.iaestudio.ai.domain.usecase.AskAiUseCase

private const val SYSTEM_PROMPT =
    "Resume el texto en máximo 5 viñetas cortas en español. No inventes información."

class SummaryViewModel(
    val askAiUseCase: AskAiUseCase
) : ViewModel() {

    private val _effect = MutableSharedFlow<SummaryEffect>()
    val effect = _effect.asSharedFlow()

    private val _state = MutableStateFlow(SummaryUiState())
    val state = _state.asStateFlow()

    fun emitEvent(event: SummaryEvent) {
        when (event) {
            is SummaryEvent.OnTextChanged -> _state.update { it.copy(text = event.value) }
            SummaryEvent.OnSummarize -> summarize()
        }
    }

    private fun emitEffect(effect: SummaryEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    private fun summarize() {
        val text = _state.value.text.trim()
        if (text.isBlank()) {
            emitEffect(SummaryEffect.ShowToast("Pega un texto para resumir"))
            return
        }
        _state.update {
            it.copy(isLoading = true, error = null, summary = "")
        }
        viewModelScope.launch {
            // Mismo caso de uso que el Tutor, pero con otra instrucción y un solo mensaje
            val result = askAiUseCase(SYSTEM_PROMPT, listOf(ChatMessage(text = text, isUser = true)))

            result.onSuccess { summary ->
                _state.update {
                    it.copy(summary = summary, isLoading = false)
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
