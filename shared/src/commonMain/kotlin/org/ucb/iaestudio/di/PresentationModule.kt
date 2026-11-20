package org.ucb.iaestudio.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.ucb.iaestudio.ai.presentation.chat.ChatViewModel
import org.ucb.iaestudio.ai.presentation.summary.SummaryViewModel
import org.ucb.iaestudio.study.presentation.dashboard.DashboardViewModel
import org.ucb.iaestudio.study.presentation.goals.GoalsViewModel
import org.ucb.iaestudio.study.presentation.sessions.SessionsViewModel

val presentationModule = module {
    viewModelOf(::DashboardViewModel)
    viewModelOf(::SessionsViewModel)
    viewModelOf(::GoalsViewModel)
    viewModelOf(::ChatViewModel)
    viewModelOf(::SummaryViewModel)
}
