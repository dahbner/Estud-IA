package org.ucb.iaestudio.study.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.ucb.iaestudio.study.domain.model.SubjectGoal
import org.ucb.iaestudio.study.domain.repository.StudyRepository

class ObserveGoalsUseCase(
    private val repository: StudyRepository
) {
    operator fun invoke(): Flow<List<SubjectGoal>> = repository.observeGoals()
}
