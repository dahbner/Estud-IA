package org.ucb.iaestudio.study.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ucb.iaestudio.study.data.dao.StudySessionDao
import org.ucb.iaestudio.study.data.dao.SubjectGoalDao
import org.ucb.iaestudio.study.data.entity.StudySessionEntity
import org.ucb.iaestudio.study.data.entity.SubjectGoalEntity
import org.ucb.iaestudio.study.domain.model.StudySession
import org.ucb.iaestudio.study.domain.model.SubjectGoal

// Lee y escribe en la base de datos Room y convierte Entity <-> Model
class StudyLocalDataSource(
    val sessionDao: StudySessionDao,
    val goalDao: SubjectGoalDao
) {
    fun observeSessions(): Flow<List<StudySession>> =
        sessionDao.observeAll().map { list -> list.map { it.toModel() } }

    suspend fun insertSession(session: StudySession) {
        sessionDao.insert(session.toEntity())
    }

    suspend fun deleteSession(id: Long) {
        sessionDao.deleteById(id)
    }

    fun observeGoals(): Flow<List<SubjectGoal>> =
        goalDao.observeAll().map { list -> list.map { it.toModel() } }

    suspend fun saveGoal(goal: SubjectGoal) {
        goalDao.upsert(goal.toEntity())
    }

    suspend fun deleteGoal(subject: String) {
        goalDao.deleteBySubject(subject)
    }

    private fun StudySessionEntity.toModel() = StudySession(
        id = id,
        subject = subject,
        minutes = minutes,
        dateMillis = dateMillis
    )

    private fun StudySession.toEntity() = StudySessionEntity(
        id = id,
        subject = subject,
        minutes = minutes,
        dateMillis = dateMillis
    )

    private fun SubjectGoalEntity.toModel() = SubjectGoal(
        subject = subject,
        weeklyMinutes = weeklyMinutes
    )

    private fun SubjectGoal.toEntity() = SubjectGoalEntity(
        subject = subject,
        weeklyMinutes = weeklyMinutes
    )
}
