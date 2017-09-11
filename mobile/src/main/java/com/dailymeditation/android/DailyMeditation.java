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

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        Fabric.with(this, new Crashlytics());
    }

    public static DailyMeditation getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return getInstance().getApplicationContext();
    }

}
