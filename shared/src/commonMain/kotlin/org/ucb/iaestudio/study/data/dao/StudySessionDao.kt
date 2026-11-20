package org.ucb.iaestudio.study.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow
import org.ucb.iaestudio.study.data.entity.StudySessionEntity

@Dao
interface StudySessionDao {
    // Flow: Room avisa a las pantallas cada vez que la tabla cambia
    @Query("SELECT * FROM study_sessions ORDER BY dateMillis DESC")
    fun observeAll(): Flow<List<StudySessionEntity>>

    @Insert
    suspend fun insert(session: StudySessionEntity)

    @Query("DELETE FROM study_sessions WHERE id = :id")
    suspend fun deleteById(id: Long)
}
