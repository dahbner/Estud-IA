package org.ucb.iaestudio.study.presentation.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ucb.iaestudio.study.domain.model.StudySession
import org.ucb.iaestudio.study.domain.model.toDurationText
import org.ucb.iaestudio.study.domain.usecase.AddSessionUseCase
import org.ucb.iaestudio.study.domain.usecase.DeleteSessionUseCase
import org.ucb.iaestudio.study.domain.usecase.ObserveGoalsUseCase
import org.ucb.iaestudio.study.domain.usecase.ObserveSessionsUseCase

class SessionsViewModel(
    val observeSessionsUseCase: ObserveSessionsUseCase,
    val observeGoalsUseCase: ObserveGoalsUseCase,
    val addSessionUseCase: AddSessionUseCase,
    val deleteSessionUseCase: DeleteSessionUseCase
) : ViewModel() {

    private val _effect = MutableSharedFlow<SessionsEffect>()
    val effect = _effect.asSharedFlow()

    private val _state = MutableStateFlow(SessionsUiState())
    val state = _state.asStateFlow()

    init {
        observeSessions()
    }

    fun emitEvent(event: SessionsEvent) {
        when (event) {
            SessionsEvent.OnAddClick -> _state.update {
                it.copy(isDialogOpen = true, subject = "", minutes = 0, formError = null)
            }
            SessionsEvent.OnDismissDialog -> _state.update { it.copy(isDialogOpen = false) }
            is SessionsEvent.OnSubjectChanged -> _state.update { it.copy(subject = event.value, formError = null) }
            is SessionsEvent.OnMinutesSelected -> _state.update { it.copy(minutes = event.value, formError = null) }
            SessionsEvent.OnSave -> save()
            is SessionsEvent.OnDelete -> viewModelScope.launch { deleteSessionUseCase(event.id) }
        }
    }

    private fun emitEffect(effect: SessionsEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    // Sugerencias: primero las materias con meta, luego las usadas recientemente
    private fun observeSessions() {
        viewModelScope.launch {
            combine(observeSessionsUseCase(), observeGoalsUseCase()) { sessions, goals ->
                val subjects = (goals.map { it.subject } + sessions.map { it.subject }).distinct().take(8)
                sessions to subjects
            }.collect { (sessions, subjects) ->
                _state.update {
                    it.copy(
                        sessions = sessions.map { session -> session.toItem() },
                        knownSubjects = subjects
                    )
                }
            }
        }
    }

    private fun save() {
        viewModelScope.launch {
            val result = addSessionUseCase(_state.value.subject, _state.value.minutes)

            result.onSuccess {
                _state.update { it.copy(isDialogOpen = false) }
                emitEffect(SessionsEffect.ShowToast("Sesión registrada"))
            }
            result.onFailure { e ->
                _state.update { it.copy(formError = e.message) }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun StudySession.toItem(): SessionItem {
        val now = Clock.System.now().toEpochMilliseconds()
        return SessionItem(
            id = id,
            subject = subject,
            durationText = minutes.toDurationText(),
            timeAgoText = timeAgo(now - dateMillis)
        )
    }

    private fun timeAgo(diffMillis: Long): String {
        val minutes = diffMillis / 60_000
        val hours = minutes / 60
        val days = hours / 24
        return when {
            minutes < 1 -> "Ahora"
            minutes < 60 -> "Hace $minutes min"
            hours < 24 -> "Hace $hours h"
            days == 1L -> "Ayer"
            else -> "Hace $days días"
        }
    }
}
