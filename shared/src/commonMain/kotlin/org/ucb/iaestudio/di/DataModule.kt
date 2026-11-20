package org.ucb.iaestudio.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.ucb.iaestudio.ai.data.agent.StudyAgentService
import org.ucb.iaestudio.ai.data.repository.AiRepositoryImpl
import org.ucb.iaestudio.ai.data.repository.AssistantRepositoryImpl
import org.ucb.iaestudio.ai.domain.repository.AssistantRepository
import org.ucb.iaestudio.ai.data.service.GeminiService
import org.ucb.iaestudio.ai.domain.repository.AiRepository
import org.ucb.iaestudio.config.AppDatabase
import org.ucb.iaestudio.study.data.dao.StudySessionDao
import org.ucb.iaestudio.study.data.dao.SubjectGoalDao
import org.ucb.iaestudio.study.data.datasource.StudyLocalDataSource
import org.ucb.iaestudio.study.data.repository.StudyRepositoryImpl
import org.ucb.iaestudio.study.domain.repository.StudyRepository

val dataModule = module {
    // IA (Koog + Gemini)
    singleOf(::GeminiService)
    single<AiRepository> { AiRepositoryImpl(get()) }

    // Agente de Koog con herramientas sobre los datos de la app
    singleOf(::StudyAgentService)
    single<AssistantRepository> { AssistantRepositoryImpl(get()) }

    // Base de datos local (Room)
    single<StudySessionDao> { get<AppDatabase>().getStudySessionDao() }
    single<SubjectGoalDao> { get<AppDatabase>().getSubjectGoalDao() }
    singleOf(::StudyLocalDataSource)
    single<StudyRepository> { StudyRepositoryImpl(get()) }
}
