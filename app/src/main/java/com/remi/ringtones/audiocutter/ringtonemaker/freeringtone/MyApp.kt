package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone

import android.app.Application
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        DataLocalManager.init(applicationContext)
        super.onCreate()
    }
}