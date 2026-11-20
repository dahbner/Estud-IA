package org.ucb.iaestudio.study.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ucb.iaestudio.study.domain.usecase.CalculateStatsUseCase
import org.ucb.iaestudio.study.domain.usecase.ObserveGoalsUseCase
import org.ucb.iaestudio.study.domain.usecase.ObserveSessionsUseCase
import org.ucb.iaestudio.study.domain.usecase.PlanStudyUseCase

class DashboardViewModel(
    val observeSessionsUseCase: ObserveSessionsUseCase,
    val observeGoalsUseCase: ObserveGoalsUseCase,
    val calculateStatsUseCase: CalculateStatsUseCase,
    val planStudyUseCase: PlanStudyUseCase
) : ViewModel() {

    private val _effect = MutableSharedFlow<DashboardEffect>()
    val effect = _effect.asSharedFlow()

    private val _state = MutableStateFlow(DashboardUiState())
    val state = _state.asStateFlow()

    init {
        observeData()
    }

    fun emitEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.OnGeneratePlan -> generatePlan()
        }
    }

    private fun emitEffect(effect: DashboardEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    // Cada vez que cambian las sesiones o las metas (en Room) se recalculan las estadísticas
    private fun observeData() {
        viewModelScope.launch {
            combine(observeSessionsUseCase(), observeGoalsUseCase()) { sessions, goals ->
                calculateStatsUseCase(sessions, goals)
            }.collect { stats ->
                _state.update { it.copy(stats = stats) }
            }
        }
    }

    private fun generatePlan() {
        val stats = _state.value.stats
        if (!stats.hasGoals) {
            emitEffect(DashboardEffect.ShowToast("Define tus metas primero"))
            return
        }
        _state.update { it.copy(isPlanning = true, error = null) }
        viewModelScope.launch {
            val result = planStudyUseCase(stats)

            result.onSuccess { plan ->
                _state.update { it.copy(plan = plan, isPlanning = false) }
            }
            result.onFailure { e ->
                _state.update { it.copy(isPlanning = false, error = e.message ?: "Error desconocido") }
            }
        }
    }
}
