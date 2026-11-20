package org.ucb.iaestudio.study.presentation.dashboard

sealed interface DashboardEvent {
    data object OnGeneratePlan : DashboardEvent
}
