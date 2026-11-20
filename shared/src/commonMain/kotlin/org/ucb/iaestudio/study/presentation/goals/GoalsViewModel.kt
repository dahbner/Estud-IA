package org.ucb.iaestudio.study.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ucb.iaestudio.study.domain.usecase.DeleteGoalUseCase
import org.ucb.iaestudio.study.domain.usecase.ObserveGoalsUseCase
import org.ucb.iaestudio.study.domain.usecase.ObserveSessionsUseCase
import org.ucb.iaestudio.study.domain.usecase.SaveGoalUseCase

private const val STEP_MINUTES = 30

class GoalsViewModel(
    val observeGoalsUseCase: ObserveGoalsUseCase,
    val observeSessionsUseCase: ObserveSessionsUseCase,
    val saveGoalUseCase: SaveGoalUseCase,
    val deleteGoalUseCase: DeleteGoalUseCase
) : ViewModel() {

    private val _effect = MutableSharedFlow<GoalsEffect>()
    val effect = _effect.asSharedFlow()

    private val _state = MutableStateFlow(GoalsUiState())
    val state = _state.asStateFlow()

    init {
        observeData()
    }

    fun emitEvent(event: GoalsEvent) {
        when (event) {
            GoalsEvent.OnAddClick -> _state.update {
                it.copy(isSheetOpen = true, subject = "", minutes = 0, formError = null)
            }
            GoalsEvent.OnDismissSheet -> _state.update { it.copy(isSheetOpen = false) }
            is GoalsEvent.OnSubjectChanged -> _state.update { it.copy(subject = event.value, formError = null) }
            is GoalsEvent.OnMinutesSelected -> _state.update { it.copy(minutes = event.value, formError = null) }
            GoalsEvent.OnSave -> save()
            is GoalsEvent.OnIncrease -> changeGoal(event.subject, STEP_MINUTES)
            is GoalsEvent.OnDecrease -> changeGoal(event.subject, -STEP_MINUTES)
            is GoalsEvent.OnDelete -> viewModelScope.launch { deleteGoalUseCase(event.subject) }
        }
    }

    private fun emitEffect(effect: GoalsEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    // Metas guardadas + materias de las sesiones que todavía no tienen meta (como sugerencias)
    private fun observeData() {
        viewModelScope.launch {
            combine(observeGoalsUseCase(), observeSessionsUseCase()) { goals, sessions ->
                val withGoal = goals.map { it.subject }.toSet()
                val suggestions = sessions.map { it.subject }.distinct().filter { it !in withGoal }.take(6)
                Triple(goals, goals.sumOf { it.weeklyMinutes }, suggestions)
            }.collect { (goals, total, suggestions) ->
                _state.update { it.copy(goals = goals, totalMinutes = total, suggestedSubjects = suggestions) }
            }
        }
    }

    private fun save() {
        viewModelScope.launch {
            val result = saveGoalUseCase(_state.value.subject, _state.value.minutes)

            result.onSuccess {
                _state.update { it.copy(isSheetOpen = false) }
                emitEffect(GoalsEffect.ShowToast("Meta guardada"))
            }
            result.onFailure { e ->
                _state.update { it.copy(formError = e.message) }
            }
        }
    }

    // Botones + y -: suben o bajan la meta de a 30 minutos
    private fun changeGoal(subject: String, delta: Int) {
        val goal = _state.value.goals.firstOrNull { it.subject == subject } ?: return
        viewModelScope.launch {
            saveGoalUseCase(subject, goal.weeklyMinutes + delta)
        }
    }
}
