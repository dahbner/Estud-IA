package org.ucb.iaestudio.ai.data.service

import ai.koog.http.client.ktor.KtorKoogHttpClient
import ai.koog.prompt.Prompt
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.llm.LLMProvider
import org.ucb.iaestudio.GEMINI_API_KEY

// Koog (JetBrains) se encarga de hablar con Gemini: arma la petición, la envía con Ktor y lee la respuesta
class GeminiService {
    // Público: el agente de Koog (StudyAgentService) reutiliza el mismo ejecutor y modelo
    val executor = MultiLLMPromptExecutor(
        LLMProvider.Google to GoogleLLMClient(
            apiKey = GEMINI_API_KEY,
            httpClientFactory = KtorKoogHttpClient.Factory()
        )
    )

    val model = GoogleModels.Gemini3_5FlashLite

    suspend fun generate(chatPrompt: Prompt): String {
        return executor.execute(chatPrompt, model).textContent()
    }
}
