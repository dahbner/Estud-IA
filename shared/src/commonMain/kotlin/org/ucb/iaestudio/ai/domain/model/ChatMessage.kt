package org.ucb.iaestudio.ai.domain.model

// toolsUsed: herramientas de la app que el agente ejecutó para responder (vacío si no usó ninguna)
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val toolsUsed: List<String> = emptyList()
)
