package org.ucb.iaestudio.study.data.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import org.ucb.iaestudio.study.data.entity.SubjectGoalEntity

@Dao
interface SubjectGoalDao {
    @Query("SELECT * FROM subject_goals ORDER BY weeklyMinutes DESC")
    fun observeAll(): Flow<List<SubjectGoalEntity>>

    // Upsert: si la materia ya tiene meta la actualiza, si no la crea
    @Upsert
    suspend fun upsert(goal: SubjectGoalEntity)

    @Query("DELETE FROM subject_goals WHERE subject = :subject")
    suspend fun deleteBySubject(subject: String)
}
