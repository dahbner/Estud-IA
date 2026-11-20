package org.ucb.iaestudio.study.domain.usecase

import org.ucb.iaestudio.ai.domain.model.ChatMessage
import org.ucb.iaestudio.ai.domain.repository.AiRepository
import org.ucb.iaestudio.study.domain.model.StudyStats
import org.ucb.iaestudio.study.domain.model.toDurationText

private const val SYSTEM_PROMPT =
    "Eres un planificador de estudio universitario. Con las metas semanales y el avance del estudiante, " +
        "responde en español exactamente con estas tres secciones:\n" +
        "**Prioridades:** las materias atrasadas, de la más a la menos urgente, con las horas que le faltan a cada una.\n" +
        "**Plan para los próximos 3 días:** Día 1, Día 2 y Día 3, cada uno con bloques concretos \"Materia – duración\" " +
        "que repartan lo que falta (máximo 3 h por día).\n" +
        "**Técnica recomendada:** una técnica de estudio concreta para la materia más atrasada, en una o dos frases.\n" +
        "Usa solo los datos recibidos. Si una meta ya está cumplida, felicita brevemente. Máximo 170 palabras."

class PlanStudyUseCase(
    private val repository: AiRepository
) {
    // Convierte las metas y el avance en texto para que la IA arme un plan concreto
    suspend operator fun invoke(stats: StudyStats): Result<String> {
        if (!stats.hasGoals) return Result.failure(Exception("Define tus metas en la pestaña Metas"))

        val data = buildString {
            appendLine("Meta semanal total: ${stats.goalMinutes.toDurationText()}.")
            appendLine("Estudiado en los últimos 7 días: ${stats.weekMinutes.toDurationText()} en ${stats.sessionCount} sesiones.")
            appendLine("Por materia (estudiado / meta / falta):")
            stats.bySubject.forEach {
                if (it.goalMinutes > 0) {
                    appendLine(
                        "- ${it.subject}: ${it.minutes.toDurationText()} / ${it.goalMinutes.toDurationText()} / " +
                            "falta ${it.remainingMinutes.toDurationText()}"
                    )
                } else {
                    appendLine("- ${it.subject}: ${it.minutes.toDurationText()} (sin meta)")
                }
            }
        }
        return repository.ask(SYSTEM_PROMPT, listOf(ChatMessage(text = data, isUser = true)))
    }
}
