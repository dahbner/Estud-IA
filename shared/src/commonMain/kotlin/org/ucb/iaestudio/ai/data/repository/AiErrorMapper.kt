package org.ucb.iaestudio.ai.data.repository

import ai.koog.http.client.KoogHttpClientException

private val NETWORK_ERRORS = listOf("IOException", "UnknownHost", "UnresolvedAddress", "Connect", "Socket")

// Convierte los errores de Koog / red en mensajes claros para el usuario
fun Throwable.toUserMessage(): String {
    val causes = generateSequence(this) { it.cause }.toList()
    val httpError = causes.filterIsInstance<KoogHttpClientException>().firstOrNull()
    val statusCode = httpError?.statusCode
    val errorBody = httpError?.errorBody.orEmpty()
    val names = causes.mapNotNull { it::class.simpleName }
    return when {
        statusCode == 429 -> "Límite alcanzado, espera un minuto"
        statusCode == 401 || statusCode == 403 || errorBody.contains("API_KEY") -> "Clave inválida"
        statusCode == 404 -> "El modelo de IA no está disponible"
        statusCode != null -> "Error $statusCode al consultar la IA"
        names.any { name -> name.contains("Timeout") } -> "La IA tardó demasiado, intenta de nuevo"
        names.any { name -> NETWORK_ERRORS.any { name.contains(it) } } -> "Sin internet. Revisa tu conexión"
        else -> "No se pudo completar la respuesta, intenta de nuevo"
    }
}
