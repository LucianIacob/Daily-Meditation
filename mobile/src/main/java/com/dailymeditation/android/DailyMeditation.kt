package com.dailymeditation.android

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class DailyMeditation : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        setupInstance()
        setupFabric()
    }

    private fun setupInstance() {
        sInstance = this
    }

    private fun setupFabric() {
        Fabric.with(this, Crashlytics())
    }

    companion object {

        private var sInstance: DailyMeditation? = null

        val appContext: Context?
            get() = sInstance?.applicationContext
    }
}