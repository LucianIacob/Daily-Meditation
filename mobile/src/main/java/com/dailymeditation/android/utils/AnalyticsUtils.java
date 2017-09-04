package com.dailymeditation.android.utils;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created with <3 by liacob & <Pi> on 04-Sep-17.
 */

public class AnalyticsUtils {

    public static void logVerseLoaded(FirebaseAnalytics firebaseAnalytics, boolean isSuccessful, String details) {
        logEvent(firebaseAnalytics, "load_verse_event", isSuccessful, details);
    }

    public static void logShareClick(FirebaseAnalytics firebaseAnalytics, boolean isSuccessfully, String displayLanguage) {
        logEvent(firebaseAnalytics, "share_button_event", isSuccessfully, displayLanguage);
    }

    public static void logFeedbackClick(FirebaseAnalytics firebaseAnalytics, boolean successfully, String displayLanguage) {
        logEvent(firebaseAnalytics, "feedback_button_event", successfully, displayLanguage);
    }

    private static void logEvent(FirebaseAnalytics analytics, String event, boolean arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(FirebaseAnalytics.Param.LEVEL, arg1);
        bundle.putString(FirebaseAnalytics.Param.VALUE, arg2);
        analytics.logEvent(event, bundle);
    }
}
