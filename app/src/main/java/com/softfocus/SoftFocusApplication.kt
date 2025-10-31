package com.softfocus

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SoftFocusApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Application initialization logic
    }
}
