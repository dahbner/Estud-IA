package org.ucb.iaestudio.study.domain.usecase

import org.ucb.iaestudio.study.domain.repository.StudyRepository

class DeleteGoalUseCase(
    private val repository: StudyRepository
) {
    suspend operator fun invoke(subject: String) {
        repository.deleteGoal(subject)
    }
}
