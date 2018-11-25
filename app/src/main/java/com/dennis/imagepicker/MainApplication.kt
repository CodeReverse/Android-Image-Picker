package com.dennis.imagepicker

import android.app.Application

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextHolder.setApplication(this)
        ContextHolder.setContext(this)
    }
}