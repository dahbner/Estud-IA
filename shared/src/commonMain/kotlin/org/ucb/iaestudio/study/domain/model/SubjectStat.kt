package org.ucb.iaestudio.study.domain.model

// Avance de una materia en los últimos 7 días comparado con su meta (0 = sin meta)
data class SubjectStat(
    val subject: String,
    val minutes: Int,
    val sessions: Int,
    val goalMinutes: Int = 0
) {
    val remainingMinutes: Int
        get() = (goalMinutes - minutes).coerceAtLeast(0)

    val progress: Float
        get() = if (goalMinutes == 0) 0f else (minutes.toFloat() / goalMinutes).coerceAtMost(1f)
}
