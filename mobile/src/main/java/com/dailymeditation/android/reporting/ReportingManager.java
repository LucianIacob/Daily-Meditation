package com.dailymeditation.android.reporting;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.dailymeditation.android.BuildConfig;
import com.dailymeditation.android.utils.Utils;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Locale;

/**
 * Created by Lucian Iacob.
 * Cluj-Napoca, 21 December, 2017.
 */

public class ReportingManager {

    public static final int STATUS_CODE_OK = 200;

    public static void logVerseLoaded(@NonNull Context context,
                                      int statusCode,
                                      boolean isSuccessful,
                                      String details) {
        if (BuildConfig.DEBUG) {
            return;
        }

        Bundle bundle = getDefaultBundle();
        bundle.putBoolean(ReportingParam.SUCCESS.name(), isSuccessful);
        bundle.putInt(ReportingParam.REASON.name(), statusCode);
        bundle.putString(ReportingParam.LANGUAGE.name(), details);

        FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.LOAD_VERSE.name(), bundle);

        Answers.getInstance().logCustom(new CustomEvent(ReportingEvent.LOAD_VERSE.name())
                .putCustomAttribute(ReportingParam.REASON.name(), statusCode));
    }

    public static void logShareClick(@NonNull Context context,
                                     boolean isSuccessfully,
                                     String location) {
        if (BuildConfig.DEBUG) {
            return;
        }

        Bundle bundle = getDefaultBundle();
        bundle.putBoolean(ReportingParam.SUCCESS.name(), isSuccessfully);
        bundle.putString(ReportingParam.LOCATION.name(), location);

        FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.CLICK_SHARE.name(), bundle);

        Answers.getInstance().logShare(new ShareEvent()
                .putMethod(Locale.getDefault().getDisplayLanguage())
                .putContentType(Utils.getCountryCode())
                .putCustomAttribute(ReportingParam.LOCATION.name(), location));
    }

    public static void logOpenFeedback(@NonNull Context context,
                                       boolean isSuccessfully) {
        if (BuildConfig.DEBUG) {
            return;
        }

        Bundle bundle = getDefaultBundle();
        bundle.putBoolean(ReportingParam.SUCCESS.name(), isSuccessfully);

        FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.OPEN_FEEDBACK.name(), bundle);

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentType(Locale.getDefault().getDisplayLanguage())
                .putCustomAttribute(ReportingParam.SUCCESS.name(), String.valueOf(isSuccessfully)));
    }

    public static void logSentFeedback(@NonNull Context context) {
        if (BuildConfig.DEBUG) {
            return;
        }

        Bundle bundle = getDefaultBundle();
        FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.SENT_FEEDBACK.name(), bundle);

        Answers.getInstance().logCustom(new CustomEvent(ReportingEvent.SENT_FEEDBACK.name())
                .putCustomAttribute(ReportingParam.DATE.name(), Utils.getCurrentDate())
                .putCustomAttribute(ReportingParam.COUNTRY.name(), Utils.getCountryCode()));
    }

    public static void logViewWidget(@NonNull Context context) {
        if (BuildConfig.DEBUG) {
            return;
        }

        Bundle bundle = getDefaultBundle();
        FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.VIEW_WIDGET.name(), bundle);

        Answers.getInstance().logCustom(new CustomEvent(ReportingEvent.VIEW_WIDGET.name())
                .putCustomAttribute(ReportingParam.DATE.name(), Utils.getCurrentDate())
                .putCustomAttribute(ReportingParam.COUNTRY.name(), Utils.getCountryCode()));
    }

    public static void logInstallWidget(@NonNull Context context) {
        if (BuildConfig.DEBUG) {
            return;
        }

        Bundle bundle = getDefaultBundle();
        FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.INSTALL_WIDGET.name(), bundle);

        Answers.getInstance().logCustom(new CustomEvent(ReportingEvent.INSTALL_WIDGET.name())
                .putCustomAttribute(ReportingParam.DATE.name(), Utils.getCurrentDate())
                .putCustomAttribute(ReportingParam.COUNTRY.name(), Utils.getCountryCode()));
    }

    public static void logUninstallWidget(@NonNull Context context) {
        if (BuildConfig.DEBUG) {
            return;
        }

        Bundle bundle = getDefaultBundle();
        FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.REMOVE_WIDGET.name(), bundle);

        Answers.getInstance().logCustom(new CustomEvent(ReportingEvent.REMOVE_WIDGET.name())
                .putCustomAttribute(ReportingParam.DATE.name(), Utils.getCurrentDate())
                .putCustomAttribute(ReportingParam.COUNTRY.name(), Utils.getCountryCode()));
    }

    public static void logErrorInterstitial(@NonNull Context context, String details) {
        if (BuildConfig.DEBUG) {
            return;
        }

        Bundle bundle = getDefaultBundle();
        bundle.putString(ReportingParam.ERROR.name(), details);

        FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.ERROR_INTERSTITIAL.name(), bundle);

        Answers.getInstance().logCustom(new CustomEvent(ReportingEvent.ERROR_INTERSTITIAL.name())
                .putCustomAttribute(ReportingParam.DATE.name(), Utils.getCurrentDate())
                .putCustomAttribute(ReportingParam.COUNTRY.name(), Utils.getCountryCode())
                .putCustomAttribute(ReportingParam.ERROR.name(), details));
    }

    public static void logShowInterstitial(@NonNull Context context) {
        if (BuildConfig.DEBUG) {
            return;
        }

        Bundle bundle = getDefaultBundle();
        FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.OPEN_INTERSTITIAL.name(), bundle);

        Answers.getInstance().logCustom(new CustomEvent(ReportingEvent.OPEN_INTERSTITIAL.name())
                .putCustomAttribute(ReportingParam.DATE.name(), Utils.getCurrentDate())
                .putCustomAttribute(ReportingParam.COUNTRY.name(), Utils.getCountryCode()));
    }

    public static void logClickInterstitial(@NonNull Context context) {
        if (BuildConfig.DEBUG) {
            return;
        }

        Bundle bundle = getDefaultBundle();
        FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.CLICK_INTERSTITIAL.name(), bundle);

        Answers.getInstance().logCustom(new CustomEvent(ReportingEvent.CLICK_INTERSTITIAL.name())
                .putCustomAttribute(ReportingParam.DATE.name(), Utils.getCurrentDate())
                .putCustomAttribute(ReportingParam.COUNTRY.name(), Utils.getCountryCode()));
    }

    @NonNull
    private static Bundle getDefaultBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(ReportingParam.DATE.name(), Utils.getCurrentDate());
        bundle.putString(ReportingParam.COUNTRY.name(), Utils.getCountryCode());
        bundle.putString(ReportingParam.LANGUAGE.name(), Locale.getDefault().getDisplayLanguage());
        return bundle;
    }
}
