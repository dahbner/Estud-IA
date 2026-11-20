package org.ucb.iaestudio.study.presentation.goals

sealed interface GoalsEffect {
    data class ShowToast(val message: String) : GoalsEffect
}
