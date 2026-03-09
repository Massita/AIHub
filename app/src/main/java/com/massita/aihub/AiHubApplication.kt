package com.massita.aihub

import android.app.Application
import com.massita.aihub.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AiHubApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AiHubApplication)
            modules(appModule)
        }
    }
}
