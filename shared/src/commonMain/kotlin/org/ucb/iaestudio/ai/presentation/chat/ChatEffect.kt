package org.ucb.iaestudio.ai.presentation.chat

sealed interface ChatEffect {
    data class ShowToast(val message: String) : ChatEffect
}
