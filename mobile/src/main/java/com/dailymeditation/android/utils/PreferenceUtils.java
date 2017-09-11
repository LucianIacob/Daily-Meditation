package com.dailymeditation.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created with <3 by liacob & <Pi> on 07-Sep-17.
 */

public class PreferenceUtils {

    private static final String TUTORIAL_PREFS = "TUTORIAL";
    private static final String TUTORIAL_SEEN = "tutorial_seen";

    public static void setSplashScreenSeen(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(TUTORIAL_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(TUTORIAL_SEEN, true).apply();
    }

    public static boolean isTutorialSeen(Context context) {
        SharedPreferences shared = context.getSharedPreferences(TUTORIAL_PREFS, MODE_PRIVATE);
        return shared.getBoolean(TUTORIAL_SEEN, false);
    }
}
