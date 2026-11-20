package org.ucb.iaestudio.study.domain.model

data class StudySession(
    val id: Long,
    val subject: String,
    val minutes: Int,
    val dateMillis: Long
)
