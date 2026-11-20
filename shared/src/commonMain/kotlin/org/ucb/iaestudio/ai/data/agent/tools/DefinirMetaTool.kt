package org.ucb.iaestudio.ai.data.agent.tools

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.serialization.typeToken
import kotlinx.serialization.Serializable
import org.ucb.iaestudio.study.domain.model.toDurationText
import org.ucb.iaestudio.study.domain.usecase.SaveGoalUseCase

// Herramienta de Koog: la IA la llama para crear o cambiar una meta semanal en Room
class DefinirMetaTool(
    private val saveGoalUseCase: SaveGoalUseCase,
    private val onUse: (String) -> Unit
) : SimpleTool<DefinirMetaTool.Args>(
    argsType = typeToken<Args>(),
    name = NAME,
    description = "Crea o actualiza la meta semanal de horas de una materia. " +
        "Úsala cuando el estudiante diga cuántas horas quiere estudiar de una materia por semana."
) {
    @Serializable
    data class Args(
        @property:LLMDescription("Nombre de la materia, por ejemplo Física")
        val materia: String,
        @property:LLMDescription("Horas por semana que quiere estudiar (se permiten decimales, por ejemplo 2.5)")
        val horasPorSemana: Double
    )

    override suspend fun execute(args: Args): String {
        onUse(NAME)
        val minutes = (args.horasPorSemana * 60).toInt()
        return saveGoalUseCase(args.materia, minutes).fold(
            onSuccess = { "Meta guardada: ${args.materia}, ${minutes.toDurationText()} por semana." },
            onFailure = { "No se pudo guardar la meta: ${it.message}" }
        )
    }

    companion object {
        const val NAME = "definir_meta"
    }
}
