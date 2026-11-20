package org.ucb.iaestudio.di

import org.koin.core.module.Module

// Cada plataforma registra aquí lo que depende de ella (en Android: el almacenamiento)
expect fun platformModule(): Module
