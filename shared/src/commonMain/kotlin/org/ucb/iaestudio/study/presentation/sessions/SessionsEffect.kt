package org.ucb.iaestudio.study.presentation.sessions

sealed interface SessionsEffect {
    data class ShowToast(val message: String) : SessionsEffect
}
