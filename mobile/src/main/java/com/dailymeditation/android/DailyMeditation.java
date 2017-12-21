package com.dailymeditation.android;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created with <3 by liacob & <Pi> on 06-Sep-17.
 */

public class DailyMeditation extends Application {

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
