package com.example.familyalarm.di

import android.app.Application
import android.util.Log
import com.chesire.lifecyklelog.LifecykleLog

class AppClass:Application() {
    override fun onCreate() {
        super.onCreate()
        LifecykleLog.initialize(this)
    }
}