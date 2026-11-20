package org.ucb.iaestudio.study.presentation.sessions

// Sesión lista para mostrarse: los textos ya vienen armados desde el ViewModel
data class SessionItem(
    val id: Long,
    val subject: String,
    val durationText: String,
    val timeAgoText: String
)
