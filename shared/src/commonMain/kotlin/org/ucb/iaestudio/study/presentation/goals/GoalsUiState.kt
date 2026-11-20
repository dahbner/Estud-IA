package org.ucb.iaestudio.study.presentation.goals

import org.ucb.iaestudio.study.domain.model.SubjectGoal

data class GoalsUiState(
    val goals: List<SubjectGoal> = emptyList(),
    val totalMinutes: Int = 0,
    val suggestedSubjects: List<String> = emptyList(),
    val isSheetOpen: Boolean = false,
    val subject: String = "",
    val minutes: Int = 0,
    val formError: String? = null
)
