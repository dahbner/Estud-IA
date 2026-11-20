package org.ucb.iaestudio.study.data.repository

import kotlinx.coroutines.flow.Flow
import org.ucb.iaestudio.study.data.datasource.StudyLocalDataSource
import org.ucb.iaestudio.study.domain.model.StudySession
import org.ucb.iaestudio.study.domain.model.SubjectGoal
import org.ucb.iaestudio.study.domain.repository.StudyRepository

class StudyRepositoryImpl(
    val local: StudyLocalDataSource
) : StudyRepository {
    override fun observeSessions(): Flow<List<StudySession>> = local.observeSessions()

    override suspend fun addSession(session: StudySession) {
        local.insertSession(session)
    }

    override suspend fun deleteSession(id: Long) {
        local.deleteSession(id)
    }

    override fun observeGoals(): Flow<List<SubjectGoal>> = local.observeGoals()

    override suspend fun saveGoal(goal: SubjectGoal) {
        local.saveGoal(goal)
    }

    override suspend fun deleteGoal(subject: String) {
        local.deleteGoal(subject)
    }
}
