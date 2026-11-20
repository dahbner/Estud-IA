package org.ucb.iaestudio.ai.data.agent.tools

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.serialization.typeToken
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.ucb.iaestudio.study.domain.model.toDurationText
import org.ucb.iaestudio.study.domain.usecase.CalculateStatsUseCase
import org.ucb.iaestudio.study.domain.usecase.ObserveGoalsUseCase
import org.ucb.iaestudio.study.domain.usecase.ObserveSessionsUseCase

// Herramienta de Koog: la IA la llama para leer el avance real del estudiante desde Room
class ConsultarProgresoTool(
    private val observeSessionsUseCase: ObserveSessionsUseCase,
    private val observeGoalsUseCase: ObserveGoalsUseCase,
    private val calculateStatsUseCase: CalculateStatsUseCase,
    private val onUse: (String) -> Unit
) : SimpleTool<ConsultarProgresoTool.Args>(
    argsType = typeToken<Args>(),
    name = NAME,
    description = "Consulta el avance real del estudiante en los últimos 7 días: tiempo estudiado, " +
        "metas semanales por materia y cuánto le falta. Úsala antes de responder sobre su progreso."
) {
    @Serializable
    data class Args(
        @property:LLMDescription("Materia específica a consultar. Déjalo vacío para ver todas.")
        val materia: String = ""
    )

    override suspend fun execute(args: Args): String {
        onUse(NAME)
        val stats = calculateStatsUseCase(observeSessionsUseCase().first(), observeGoalsUseCase().first())
        val subjects = stats.bySubject.filter {
            args.materia.isBlank() || it.subject.equals(args.materia.trim(), ignoreCase = true)
        }
        return buildString {
            appendLine("Meta semanal total: ${if (stats.hasGoals) stats.goalMinutes.toDurationText() else "sin metas"}.")
            appendLine("Estudiado en 7 días: ${stats.weekMinutes.toDurationText()} en ${stats.sessionCount} sesiones.")
            if (stats.hasGoals) appendLine("Falta en total: ${stats.remainingMinutes.toDurationText()}.")
            if (subjects.isEmpty()) appendLine("No hay datos de esa materia.")
            subjects.forEach {
                if (it.goalMinutes > 0) {
                    appendLine(
                        "- ${it.subject}: ${it.minutes.toDurationText()} de ${it.goalMinutes.toDurationText()}, " +
                            "falta ${it.remainingMinutes.toDurationText()}"
                    )
                } else {
                    appendLine("- ${it.subject}: ${it.minutes.toDurationText()} (sin meta)")
                }
            }
        }
    }

    companion object {
        const val NAME = "consultar_progreso"
    }
}
