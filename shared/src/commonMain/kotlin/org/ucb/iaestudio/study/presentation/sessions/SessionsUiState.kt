package org.ucb.iaestudio.study.presentation.sessions

data class SessionsUiState(
    val sessions: List<SessionItem> = emptyList(),
    val knownSubjects: List<String> = emptyList(),
    val isDialogOpen: Boolean = false,
    val subject: String = "",
    val minutes: Int = 0,
    val formError: String? = null
)
