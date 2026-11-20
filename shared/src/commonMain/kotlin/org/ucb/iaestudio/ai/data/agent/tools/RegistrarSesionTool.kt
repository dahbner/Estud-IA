package org.ucb.iaestudio.ai.data.agent.tools

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.serialization.typeToken
import kotlinx.serialization.Serializable
import org.ucb.iaestudio.study.domain.model.toDurationText
import org.ucb.iaestudio.study.domain.usecase.AddSessionUseCase

// Herramienta de Koog: la IA la llama para guardar una sesión de estudio en Room
class RegistrarSesionTool(
    private val addSessionUseCase: AddSessionUseCase,
    private val onUse: (String) -> Unit
) : SimpleTool<RegistrarSesionTool.Args>(
    argsType = typeToken<Args>(),
    name = NAME,
    description = "Registra una sesión de estudio que el estudiante ya hizo. " +
        "Úsala cuando diga que estudió algo, por ejemplo: 'hoy estudié 45 minutos de Cálculo'."
) {
    @Serializable
    data class Args(
        @property:LLMDescription("Nombre de la materia estudiada, por ejemplo Cálculo")
        val materia: String,
        @property:LLMDescription("Duración de la sesión en minutos (1 hora = 60)")
        val minutos: Int
    )

    override suspend fun execute(args: Args): String {
        onUse(NAME)
        return addSessionUseCase(args.materia, args.minutos).fold(
            onSuccess = { "Sesión registrada: ${args.materia}, ${args.minutos.toDurationText()}." },
            onFailure = { "No se pudo registrar: ${it.message}" }
        )
    }

    companion object {
        const val NAME = "registrar_sesion"
    }
}
