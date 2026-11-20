package org.ucb.iaestudio.ai.data.agent.tools

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.serialization.typeToken
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.ucb.iaestudio.study.domain.model.toDurationText
import org.ucb.iaestudio.study.domain.usecase.ObserveSessionsUseCase

// Herramienta de Koog: la IA la llama para revisar el historial de sesiones guardado en Room
class ListarSesionesTool(
    private val observeSessionsUseCase: ObserveSessionsUseCase,
    private val onUse: (String) -> Unit
) : SimpleTool<ListarSesionesTool.Args>(
    argsType = typeToken<Args>(),
    name = NAME,
    description = "Lista las sesiones de estudio más recientes del estudiante con materia, duración y hace cuánto fueron."
) {
    @Serializable
    data class Args(
        @property:LLMDescription("Cuántas sesiones recientes mostrar (por defecto 10)")
        val cantidad: Int = 10
    )

    @OptIn(ExperimentalTime::class)
    override suspend fun execute(args: Args): String {
        onUse(NAME)
        val sessions = observeSessionsUseCase().first().take(args.cantidad.coerceIn(1, 30))
        if (sessions.isEmpty()) return "El estudiante todavía no registró sesiones."

        val now = Clock.System.now().toEpochMilliseconds()
        return sessions.joinToString(separator = "\n") {
            val hours = (now - it.dateMillis) / 3_600_000
            val whenText = when {
                hours < 1 -> "hace menos de una hora"
                hours < 24 -> "hace $hours h"
                else -> "hace ${hours / 24} días"
            }
            "- ${it.subject}: ${it.minutes.toDurationText()} ($whenText)"
        }
    }

    companion object {
        const val NAME = "listar_sesiones"
    }
}
