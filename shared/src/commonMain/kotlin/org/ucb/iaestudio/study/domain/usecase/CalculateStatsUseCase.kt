package org.ucb.iaestudio.study.domain.usecase

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import org.ucb.iaestudio.study.domain.model.StudySession
import org.ucb.iaestudio.study.domain.model.StudyStats
import org.ucb.iaestudio.study.domain.model.SubjectGoal
import org.ucb.iaestudio.study.domain.model.SubjectStat

private const val WEEK_MILLIS = 7L * 24 * 60 * 60 * 1000

class CalculateStatsUseCase {
    // Junta las sesiones de los últimos 7 días con las metas que definió el usuario
    @OptIn(ExperimentalTime::class)
    operator fun invoke(sessions: List<StudySession>, goals: List<SubjectGoal>): StudyStats {
        val now = Clock.System.now().toEpochMilliseconds()
        val week = sessions.filter { now - it.dateMillis < WEEK_MILLIS }
        val studied = week.groupBy { it.subject }
        val goalsBySubject = goals.associate { it.subject to it.weeklyMinutes }

        // Materias con meta (aunque no se hayan estudiado) y materias estudiadas sin meta
        val subjects = goals.map { it.subject } + studied.keys.filter { it !in goalsBySubject }
        val bySubject = subjects.map { subject ->
            val list = studied[subject].orEmpty()
            SubjectStat(
                subject = subject,
                minutes = list.sumOf { it.minutes },
                sessions = list.size,
                goalMinutes = goalsBySubject[subject] ?: 0
            )
        }.sortedWith(compareByDescending<SubjectStat> { it.goalMinutes > 0 }.thenByDescending { it.remainingMinutes })

        return StudyStats(
            weekMinutes = week.sumOf { it.minutes },
            goalMinutes = goals.sumOf { it.weeklyMinutes },
            sessionCount = week.size,
            bySubject = bySubject
        )
    }
}
