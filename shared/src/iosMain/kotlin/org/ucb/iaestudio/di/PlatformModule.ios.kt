package org.ucb.iaestudio.di

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.core.module.Module
import org.koin.dsl.module
import org.ucb.iaestudio.config.AppDatabase
import platform.Foundation.NSHomeDirectory

// iOS no se usa en este proyecto; se deja configurado solo para que la base exista
actual fun platformModule(): Module = module {
    single<AppDatabase> {
        Room.databaseBuilder<AppDatabase>(name = NSHomeDirectory() + "/estudia.db")
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
}
