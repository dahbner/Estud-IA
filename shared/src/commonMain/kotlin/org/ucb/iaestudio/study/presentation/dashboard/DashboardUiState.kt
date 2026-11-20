package org.ucb.iaestudio.study.presentation.dashboard

import org.ucb.iaestudio.study.domain.model.StudyStats

data class DashboardUiState(
    val stats: StudyStats = StudyStats(),
    val plan: String = "",
    val isPlanning: Boolean = false,
    val error: String? = null
)
