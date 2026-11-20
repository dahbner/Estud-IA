package org.ucb.iaestudio.ai.domain.repository

import org.ucb.iaestudio.ai.domain.model.AgentReply
import org.ucb.iaestudio.ai.domain.model.ChatMessage

interface AssistantRepository {
    suspend fun chat(history: List<ChatMessage>): Result<AgentReply>
}
