package com.dailymeditation.android;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.faithcomesbyhearing.dbt.Dbt;

import io.fabric.sdk.android.Fabric;

/**
 * Created with <3 by liacob & <Pi> on 06-Sep-17.
 */

public class DailyMeditation extends Application {

    private static DailyMeditation sInstance;

    public static DailyMeditation getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return getInstance().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupInstance();
        setupFabric();
        setupDbt();
    }

    private void setupInstance() {
        sInstance = this;
    }

    private void setupFabric() {
        Fabric.with(this, new Crashlytics());
    }

    private void setupDbt() {
        Dbt.setApiKey(BuildConfig.DBT_KEY);
    }

}
