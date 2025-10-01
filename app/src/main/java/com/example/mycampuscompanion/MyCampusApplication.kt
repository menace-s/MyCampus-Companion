package com.example.mycampuscompanion

import android.app.Application
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig

class MyCampusApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Configuration essentielle pour osmdroid
        Configuration.getInstance().userAgentValue = this.packageName
    }
}