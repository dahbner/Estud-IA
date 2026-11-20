package org.ucb.iaestudio.ai.data.repository

import kotlinx.coroutines.CancellationException
import org.ucb.iaestudio.ai.data.agent.StudyAgentService
import org.ucb.iaestudio.ai.domain.model.AgentReply
import org.ucb.iaestudio.ai.domain.model.ChatMessage
import org.ucb.iaestudio.ai.domain.repository.AssistantRepository

class AssistantRepositoryImpl(
    val agent: StudyAgentService
) : AssistantRepository {
    override suspend fun chat(history: List<ChatMessage>): Result<AgentReply> {
        return try {
            val reply = agent.chat(history)
            if (reply.text.isBlank()) {
                Result.failure(Exception("La IA no devolvió ninguna respuesta"))
            } else {
                Result.success(reply)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println("AssistantRepository: ${e::class.simpleName}: ${e.message}")
            Result.failure(Exception(e.toUserMessage()))
        }
    }
}
