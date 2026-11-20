package org.ucb.iaestudio.ai.presentation.summary

sealed interface SummaryEffect {
    data class ShowToast(val message: String) : SummaryEffect
}
