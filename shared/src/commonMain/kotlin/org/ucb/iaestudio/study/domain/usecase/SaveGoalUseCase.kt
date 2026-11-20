package org.ucb.iaestudio.study.domain.usecase

import org.ucb.iaestudio.study.domain.model.SubjectGoal
import org.ucb.iaestudio.study.domain.repository.StudyRepository

private const val MIN_GOAL_MINUTES = 30
private const val MAX_GOAL_MINUTES = 40 * 60

class SaveGoalUseCase(
    private val repository: StudyRepository
) {
    // Crea o actualiza la meta semanal de una materia (entre 30 min y 40 h)
    suspend operator fun invoke(subject: String, weeklyMinutes: Int): Result<Unit> {
        val cleanSubject = subject.trim().replaceFirstChar { it.uppercase() }
        if (cleanSubject.isBlank()) return Result.failure(Exception("Escribe la materia"))
        if (weeklyMinutes < MIN_GOAL_MINUTES) return Result.failure(Exception("Elige cuántas horas por semana"))

        val minutes = weeklyMinutes.coerceAtMost(MAX_GOAL_MINUTES)
        repository.saveGoal(SubjectGoal(subject = cleanSubject, weeklyMinutes = minutes))
        return Result.success(Unit)
    }
}
