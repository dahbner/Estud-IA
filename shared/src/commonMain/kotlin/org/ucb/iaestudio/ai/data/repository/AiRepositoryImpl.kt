package org.ucb.iaestudio.ai.data.repository

import ai.koog.prompt.Prompt
import ai.koog.prompt.dsl.prompt
import kotlinx.coroutines.CancellationException
import org.ucb.iaestudio.ai.data.service.GeminiService
import org.ucb.iaestudio.ai.domain.model.ChatMessage
import org.ucb.iaestudio.ai.domain.repository.AiRepository

class AiRepositoryImpl(
    val service: GeminiService
) : AiRepository {
    override suspend fun ask(systemPrompt: String, history: List<ChatMessage>): Result<String> {
        return try {
            val answer = service.generate(buildPrompt(systemPrompt, history))
            if (answer.isBlank()) {
                Result.failure(Exception("La IA no devolvió ninguna respuesta"))
            } else {
                Result.success(answer)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println("AiRepository: ${e.message}")
            Result.failure(Exception(e.toUserMessage()))
        }
    }

    // Se reenvía toda la conversación: así la IA "recuerda" lo que se habló
    private fun buildPrompt(systemPrompt: String, history: List<ChatMessage>): Prompt {
        return prompt("chat") {
            system(systemPrompt)
            history.forEach { message ->
                if (message.isUser) user(message.text) else assistant(message.text)
            }
        }
    }
}
