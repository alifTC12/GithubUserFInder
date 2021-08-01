package com.aliftc12.githubuserfinder

import android.app.Application
import com.aliftc12.githubuserfinder.di.userFinderModule
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(userFinderModule)
        }
    }
}