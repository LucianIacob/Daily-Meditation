package com.dailymeditation.android.utils.firebase;

import android.content.Context;
import android.os.Bundle;

import com.dailymeditation.android.BuildConfig;
import com.dailymeditation.android.utils.Utils;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created with <3 by liacob & <Pi> on 04-Sep-17.
 */

public class AnalyticsUtils {

    private static final String EVENT_LOAD_VERSE = "load_verse_event";
    private static final String EVENT_SHARE_CLICK = "share_button_event";
    private static final String EVENT_FEEDBACK_OPEN = "feedback_button_event";
    private static final String EVENT_FEEDBACK_SENT = "send_feedback_event";
    private static final String EVENT_INTERSTITIAL_ERR = "interstitial_error";

    private static final String PARAM_DATE = "event_date";
    private static final String PARAM_COUNTRY = "country";
    private static final String PARAM_VALID = "verse_loaded";
    private static final String PARAM_INTERSTITIAL_ERR = "interstitial_error";
    private static final String EVENT_INTERSTITIAL = "interstitial_displayed";
    private static final String EVENT_INTERSTITIAL_CLICK = "interstitial_click";
    private static final String PARAM_LANGUAGE = "language";

    public static void logVerseLoaded(Context context, boolean isSuccessful, String details) {
        logEvent(context, EVENT_LOAD_VERSE, isSuccessful, details);
    }

    public static void logShareClick(Context context, boolean isSuccessfully, String displayLanguage) {
        logEvent(context, EVENT_SHARE_CLICK, isSuccessfully, displayLanguage);
    }

    public static void logFeedbackClick(Context context, boolean successfully, String displayLanguage) {
        logEvent(context, EVENT_FEEDBACK_OPEN, successfully, displayLanguage);
    }

    public static void logSendFeedback(Context context) {
        if (!BuildConfig.DEBUG) {
            Bundle bundle = new Bundle();
            bundle.putString(PARAM_DATE, Utils.getCurrentDate());
            bundle.putString(PARAM_COUNTRY, Utils.getCountryCode());

            FirebaseAnalytics.getInstance(context).logEvent(EVENT_FEEDBACK_SENT, bundle);
        }
    }

    private static void logEvent(Context context, String event, boolean isSuccessfully, String language) {
        if (!BuildConfig.DEBUG) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(PARAM_VALID, isSuccessfully);
            bundle.putString(PARAM_DATE, Utils.getCurrentDate());
            bundle.putString(PARAM_COUNTRY, Utils.getCountryCode());
            bundle.putString(PARAM_LANGUAGE, language);

            FirebaseAnalytics.getInstance(context).logEvent(event, bundle);
        }
    }

    public static void logInterstitialFailed(Context context, String error) {
        if (!BuildConfig.DEBUG) {
            Bundle bundle = new Bundle();
            bundle.putString(PARAM_INTERSTITIAL_ERR, error);
            bundle.putString(PARAM_DATE, Utils.getCurrentDate());
            bundle.putString(PARAM_COUNTRY, Utils.getCountryCode());

            FirebaseAnalytics.getInstance(context).logEvent(EVENT_INTERSTITIAL_ERR, bundle);
        }
    }

    public static void logInterstitialDisplayed(Context context) {
        if (!BuildConfig.DEBUG) {
            Bundle bundle = new Bundle();
            bundle.putString(PARAM_DATE, Utils.getCurrentDate());
            bundle.putString(PARAM_COUNTRY, Utils.getCountryCode());

            FirebaseAnalytics.getInstance(context).logEvent(EVENT_INTERSTITIAL, bundle);
        }
    }

    public static void logInterstitialClick(Context context) {
        if (!BuildConfig.DEBUG) {
            Bundle bundle = new Bundle();
            bundle.putString(PARAM_DATE, Utils.getCurrentDate());
            bundle.putString(PARAM_COUNTRY, Utils.getCountryCode());

            FirebaseAnalytics.getInstance(context).logEvent(EVENT_INTERSTITIAL_CLICK, bundle);
        }
    }
}
