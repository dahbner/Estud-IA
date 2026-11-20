package org.ucb.iaestudio.study.presentation.goals

sealed interface GoalsEvent {
    data object OnAddClick : GoalsEvent
    data object OnDismissSheet : GoalsEvent
    data class OnSubjectChanged(val value: String) : GoalsEvent
    data class OnMinutesSelected(val value: Int) : GoalsEvent
    data object OnSave : GoalsEvent
    data class OnIncrease(val subject: String) : GoalsEvent
    data class OnDecrease(val subject: String) : GoalsEvent
    data class OnDelete(val subject: String) : GoalsEvent
}
