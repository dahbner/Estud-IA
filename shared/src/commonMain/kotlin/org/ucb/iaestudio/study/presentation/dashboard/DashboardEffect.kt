package org.ucb.iaestudio.study.presentation.dashboard

sealed interface DashboardEffect {
    data class ShowToast(val message: String) : DashboardEffect
}
