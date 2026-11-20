package org.ucb.iaestudio.ai.domain.usecase

import org.ucb.iaestudio.ai.domain.model.AgentReply
import org.ucb.iaestudio.ai.domain.model.ChatMessage
import org.ucb.iaestudio.ai.domain.repository.AssistantRepository

class ChatWithAssistantUseCase(
    private val repository: AssistantRepository
) {
    suspend operator fun invoke(history: List<ChatMessage>): Result<AgentReply> {
        return repository.chat(history)
    }
}
