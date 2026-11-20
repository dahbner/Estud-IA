package org.ucb.iaestudio.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.ucb.iaestudio.ai.domain.usecase.AskAiUseCase
import org.ucb.iaestudio.ai.domain.usecase.ChatWithAssistantUseCase
import org.ucb.iaestudio.study.domain.usecase.AddSessionUseCase
import org.ucb.iaestudio.study.domain.usecase.CalculateStatsUseCase
import org.ucb.iaestudio.study.domain.usecase.DeleteGoalUseCase
import org.ucb.iaestudio.study.domain.usecase.DeleteSessionUseCase
import org.ucb.iaestudio.study.domain.usecase.ObserveGoalsUseCase
import org.ucb.iaestudio.study.domain.usecase.ObserveSessionsUseCase
import org.ucb.iaestudio.study.domain.usecase.PlanStudyUseCase
import org.ucb.iaestudio.study.domain.usecase.SaveGoalUseCase

val domainModule = module {
    singleOf(::AskAiUseCase)
    singleOf(::ChatWithAssistantUseCase)
    singleOf(::ObserveSessionsUseCase)
    singleOf(::AddSessionUseCase)
    singleOf(::DeleteSessionUseCase)
    singleOf(::ObserveGoalsUseCase)
    singleOf(::SaveGoalUseCase)
    singleOf(::DeleteGoalUseCase)
    singleOf(::CalculateStatsUseCase)
    singleOf(::PlanStudyUseCase)
}
