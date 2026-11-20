package org.ucb.iaestudio

import android.app.Application
import org.ucb.iaestudio.di.initKoinAndroid

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinAndroid(this)
    }
}
