package com.dailymeditation.android;

import android.app.Application;
import android.content.Context;

/**
 * Created with <3 by liacob & <Pi> on 06-Sep-17.
 */

public class DailyMeditation extends Application {

    private static DailyMeditation sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static DailyMeditation getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return getInstance().getApplicationContext();
    }

}
