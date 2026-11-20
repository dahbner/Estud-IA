package org.ucb.iaestudio.study.domain.usecase

import org.ucb.iaestudio.study.domain.repository.StudyRepository

class DeleteSessionUseCase(
    private val repository: StudyRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteSession(id)
    }
}
