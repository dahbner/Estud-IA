package org.ucb.iaestudio.ai.presentation.summary

data class SummaryUiState(
    val text: String = "",
    val summary: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
