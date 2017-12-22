package com.dailymeditation.android;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.dailymeditation.android.utils.AdUtils;
import com.google.android.gms.ads.MobileAds;

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
        setupAds();
        setupFabric();
    }

    private void setupInstance() {
        sInstance = this;
    }

    private void setupAds() {
        MobileAds.initialize(this, AdUtils.ADS_APP_ID);
    }

    private void setupFabric() {
        Fabric.with(this, new Crashlytics());
    }

}
