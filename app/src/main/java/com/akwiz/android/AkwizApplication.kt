package com.akwiz.android

import android.app.Application
import com.akwiz.android.di.AppContainer

class AkwizApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
