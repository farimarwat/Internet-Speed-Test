package com.marwatsoft.speedtestmaster.startup

import android.content.Context
import androidx.startup.Initializer
import com.marwatsoft.speedtestmaster.BuildConfig
import timber.log.Timber

class Timberinitializer : Initializer<Unit> {
    override fun create(context: Context) {
        if(BuildConfig.DEBUGMODE){
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}