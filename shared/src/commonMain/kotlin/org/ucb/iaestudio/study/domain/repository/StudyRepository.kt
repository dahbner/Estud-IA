package org.ucb.iaestudio.study.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ucb.iaestudio.study.domain.model.StudySession
import org.ucb.iaestudio.study.domain.model.SubjectGoal

interface StudyRepository {
    fun observeSessions(): Flow<List<StudySession>>
    suspend fun addSession(session: StudySession)
    suspend fun deleteSession(id: Long)

    fun observeGoals(): Flow<List<SubjectGoal>>
    suspend fun saveGoal(goal: SubjectGoal)
    suspend fun deleteGoal(subject: String)
}
