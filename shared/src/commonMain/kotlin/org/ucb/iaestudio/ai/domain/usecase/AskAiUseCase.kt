package org.ucb.iaestudio.ai.domain.usecase

import org.ucb.iaestudio.ai.domain.model.ChatMessage
import org.ucb.iaestudio.ai.domain.repository.AiRepository

class AskAiUseCase(
    private val repository: AiRepository
) {
    suspend operator fun invoke(systemPrompt: String, history: List<ChatMessage>): Result<String> {
        return repository.ask(systemPrompt, history)
    }
}
