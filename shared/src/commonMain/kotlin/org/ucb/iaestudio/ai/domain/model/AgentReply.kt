package org.ucb.iaestudio.ai.domain.model

// Respuesta del agente: el texto final y las herramientas que usó para llegar a él
data class AgentReply(
    val text: String,
    val toolsUsed: List<String>
)
