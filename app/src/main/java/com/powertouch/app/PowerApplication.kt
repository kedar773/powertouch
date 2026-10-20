package com.powertouch.app

import android.app.Application

class PowerApplication : Application() {
    lateinit var preferences: PreferencesManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferences = PreferencesManager(this)
    }

    companion object {
        lateinit var instance: PowerApplication
            private set
    }
}
