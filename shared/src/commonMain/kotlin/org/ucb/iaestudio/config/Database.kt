package org.ucb.iaestudio.config

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import org.ucb.iaestudio.study.data.dao.StudySessionDao
import org.ucb.iaestudio.study.data.dao.SubjectGoalDao
import org.ucb.iaestudio.study.data.entity.StudySessionEntity
import org.ucb.iaestudio.study.data.entity.SubjectGoalEntity

@Database(entities = [StudySessionEntity::class, SubjectGoalEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getStudySessionDao(): StudySessionDao
    abstract fun getSubjectGoalDao(): SubjectGoalDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
