package org.ucb.iaestudio.study.data.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val minutes: Int,
    val dateMillis: Long
)
