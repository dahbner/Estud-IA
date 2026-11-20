package org.ucb.iaestudio.study.domain.model

// Resumen de los últimos 7 días que se muestra en Inicio y se envía a la IA
data class StudyStats(
    val weekMinutes: Int = 0,
    val goalMinutes: Int = 0,
    val sessionCount: Int = 0,
    val bySubject: List<SubjectStat> = emptyList()
) {
    val hasGoals: Boolean
        get() = goalMinutes > 0

    val remainingMinutes: Int
        get() = (goalMinutes - weekMinutes).coerceAtLeast(0)

    val progress: Float
        get() = if (goalMinutes == 0) 0f else (weekMinutes.toFloat() / goalMinutes).coerceAtMost(1f)
}
