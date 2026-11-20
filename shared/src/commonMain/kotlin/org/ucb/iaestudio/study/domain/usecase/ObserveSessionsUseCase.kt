package org.ucb.iaestudio.study.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.ucb.iaestudio.study.domain.model.StudySession
import org.ucb.iaestudio.study.domain.repository.StudyRepository

class ObserveSessionsUseCase(
    private val repository: StudyRepository
) {
    operator fun invoke(): Flow<List<StudySession>> = repository.observeSessions()
}
