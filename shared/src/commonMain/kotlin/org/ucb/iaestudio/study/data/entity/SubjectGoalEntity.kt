package org.ucb.iaestudio.study.data.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// Una fila por materia: la materia es la llave, así no se repite
@Entity(tableName = "subject_goals")
data class SubjectGoalEntity(
    @PrimaryKey
    val subject: String,
    val weeklyMinutes: Int
)
