package org.ucb.iaestudio.study.presentation.sessions

sealed interface SessionsEvent {
    data object OnAddClick : SessionsEvent
    data object OnDismissDialog : SessionsEvent
    data class OnSubjectChanged(val value: String) : SessionsEvent
    data class OnMinutesSelected(val value: Int) : SessionsEvent
    data object OnSave : SessionsEvent
    data class OnDelete(val id: Long) : SessionsEvent
}
