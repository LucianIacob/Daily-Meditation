package com.dailymeditation.android;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created with <3 by liacob & <Pi> on 06-Sep-17.
 */

public class DailyMeditation extends MultiDexApplication {

    private static DailyMeditation sInstance;

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupInstance();
        setupFabric();
    }

    private void setupInstance() {
        sInstance = this;
    }

    private void setupFabric() {
        Fabric.with(this, new Crashlytics());
    }

}
