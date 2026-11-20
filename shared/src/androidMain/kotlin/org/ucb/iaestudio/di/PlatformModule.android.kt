package org.ucb.iaestudio.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.ucb.iaestudio.config.AppDatabase
import org.ucb.iaestudio.config.getDatabaseBuilder

actual fun platformModule(): Module = module {
    single<AppDatabase> {
        getDatabaseBuilder(get())
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
}
