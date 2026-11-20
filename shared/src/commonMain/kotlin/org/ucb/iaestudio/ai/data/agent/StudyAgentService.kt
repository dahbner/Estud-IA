package org.ucb.iaestudio.ai.data.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.dsl.prompt
import org.ucb.iaestudio.ai.data.agent.tools.ConsultarProgresoTool
import org.ucb.iaestudio.ai.data.agent.tools.DefinirMetaTool
import org.ucb.iaestudio.ai.data.agent.tools.ListarSesionesTool
import org.ucb.iaestudio.ai.data.agent.tools.RegistrarSesionTool
import org.ucb.iaestudio.ai.data.service.GeminiService
import org.ucb.iaestudio.ai.domain.model.AgentReply
import org.ucb.iaestudio.ai.domain.model.ChatMessage
import org.ucb.iaestudio.study.domain.usecase.AddSessionUseCase
import org.ucb.iaestudio.study.domain.usecase.CalculateStatsUseCase
import org.ucb.iaestudio.study.domain.usecase.ObserveGoalsUseCase
import org.ucb.iaestudio.study.domain.usecase.ObserveSessionsUseCase
import org.ucb.iaestudio.study.domain.usecase.SaveGoalUseCase

private const val MAX_ITERATIONS = 12

private const val SYSTEM_PROMPT =
    "Eres Estud-IA, el asistente de estudio de esta app móvil. Respondes en español, claro y breve (máximo 150 palabras).\n" +
        "Tienes herramientas para consultar y modificar los datos reales del estudiante guardados en la app:\n" +
        "- Si pregunta por su progreso, metas o cuánto le falta, usa consultar_progreso antes de responder.\n" +
        "- Si dice que estudió algo, regístralo con registrar_sesion y confírmalo.\n" +
        "- Si quiere fijar o cambiar cuántas horas estudiar de una materia, usa definir_meta.\n" +
        "- Si pregunta qué estudió antes, usa listar_sesiones.\n" +
        "Nunca inventes datos del estudiante: consúltalos con las herramientas. " +
        "Si te pide explicar un tema, explícalo paso a paso con un ejemplo."

// Agente de Koog (JetBrains): la IA decide sola qué herramienta de la app ejecutar para responder
class StudyAgentService(
    private val gemini: GeminiService,
    private val observeSessionsUseCase: ObserveSessionsUseCase,
    private val observeGoalsUseCase: ObserveGoalsUseCase,
    private val calculateStatsUseCase: CalculateStatsUseCase,
    private val addSessionUseCase: AddSessionUseCase,
    private val saveGoalUseCase: SaveGoalUseCase
) {
    suspend fun chat(history: List<ChatMessage>): AgentReply {
        // Cada herramienta anota su nombre aquí cuando la IA la usa, para mostrarlo en el chat
        val toolsUsed = mutableListOf<String>()
        val onUse: (String) -> Unit = { toolsUsed.add(it) }

        val toolRegistry = ToolRegistry {
            tool(ConsultarProgresoTool(observeSessionsUseCase, observeGoalsUseCase, calculateStatsUseCase, onUse))
            tool(RegistrarSesionTool(addSessionUseCase, onUse))
            tool(DefinirMetaTool(saveGoalUseCase, onUse))
            tool(ListarSesionesTool(observeSessionsUseCase, onUse))
        }

        // La conversación anterior va en el prompt (memoria); la última pregunta es la entrada del agente
        val previous = history.dropLast(1)
        val question = history.last().text

        val agent = AIAgent(
            promptExecutor = gemini.executor,
            agentConfig = AIAgentConfig(
                prompt = prompt("estud-ia") {
                    system(SYSTEM_PROMPT)
                    previous.forEach { message ->
                        if (message.isUser) user(message.text) else assistant(message.text)
                    }
                },
                model = gemini.model,
                maxAgentIterations = MAX_ITERATIONS
            ),
            toolRegistry = toolRegistry
        )

        val answer = agent.run(question)
        return AgentReply(text = answer, toolsUsed = toolsUsed.distinct())
    }
}
