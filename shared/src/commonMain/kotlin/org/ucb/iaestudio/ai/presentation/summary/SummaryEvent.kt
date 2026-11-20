package org.ucb.iaestudio.ai.presentation.summary

sealed interface SummaryEvent {
    data class OnTextChanged(val value: String) : SummaryEvent
    data object OnSummarize : SummaryEvent
}
