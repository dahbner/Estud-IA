package org.ucb.iaestudio.ai.domain.repository

import org.ucb.iaestudio.ai.domain.model.ChatMessage

interface AiRepository {
    suspend fun ask(systemPrompt: String, history: List<ChatMessage>): Result<String>
}
