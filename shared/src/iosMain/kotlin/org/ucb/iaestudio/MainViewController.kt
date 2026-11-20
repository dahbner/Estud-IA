package org.ucb.iaestudio

import androidx.compose.ui.window.ComposeUIViewController
import org.ucb.iaestudio.di.initKoin

fun MainViewController() = ComposeUIViewController(configure = { initKoin() }) { App() }