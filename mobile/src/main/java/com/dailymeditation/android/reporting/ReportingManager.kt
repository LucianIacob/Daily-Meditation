package com.dailymeditation.android.reporting

import android.content.Context
import androidx.core.os.bundleOf
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.crashlytics.android.answers.CustomEvent
import com.crashlytics.android.answers.ShareEvent
import com.dailymeditation.android.BuildConfig
import com.dailymeditation.android.utils.getCountryCode
import com.dailymeditation.android.utils.getCurrentDate
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*

object ReportingManager {

    const val STATUS_CODE_OK = 200

    fun logVerseLoaded(
        context: Context,
        statusCode: Int,
        isSuccessful: Boolean,
        details: String?
    ) {
        if (BuildConfig.DEBUG) {
            return
        }
        val bundle = getDefaultBundle()
        bundle.putBoolean(ReportingParam.SUCCESS.name, isSuccessful)
        bundle.putInt(ReportingParam.REASON.name, statusCode)
        bundle.putString(ReportingParam.LANGUAGE.name, details)
        FirebaseAnalytics.getInstance(context)
            .logEvent(ReportingEvent.LOAD_VERSE.name, bundle)
        Answers.getInstance()
            .logCustom(
                CustomEvent(ReportingEvent.LOAD_VERSE.name)
                    .putCustomAttribute(ReportingParam.REASON.name, statusCode)
            )
    }

    fun logShareClick(
        context: Context,
        isSuccessfully: Boolean,
        location: String?
    ) {
        if (BuildConfig.DEBUG) {
            return
        }
        val bundle = getDefaultBundle()
        bundle.putBoolean(ReportingParam.SUCCESS.name, isSuccessfully)
        bundle.putString(ReportingParam.LOCATION.name, location)
        FirebaseAnalytics.getInstance(context)
            .logEvent(ReportingEvent.CLICK_SHARE.name, bundle)
        Answers.getInstance()
            .logShare(
                ShareEvent()
                    .putMethod(Locale.getDefault().displayLanguage)
                    .putContentType(context.getCountryCode())
                    .putCustomAttribute(ReportingParam.LOCATION.name, location)
            )
    }

    fun logOpenFeedback(
        context: Context,
        isSuccessfully: Boolean
    ) {
        if (BuildConfig.DEBUG) {
            return
        }
        val bundle = getDefaultBundle()
        bundle.putBoolean(ReportingParam.SUCCESS.name, isSuccessfully)
        FirebaseAnalytics.getInstance(context)
            .logEvent(ReportingEvent.OPEN_FEEDBACK.name, bundle)
        Answers.getInstance()
            .logContentView(
                ContentViewEvent()
                    .putContentType(Locale.getDefault().displayLanguage)
                    .putCustomAttribute(ReportingParam.SUCCESS.name, isSuccessfully.toString())
            )
    }

    fun logSentFeedback(context: Context) {
        if (BuildConfig.DEBUG) {
            return
        }
        val bundle = getDefaultBundle()
        FirebaseAnalytics.getInstance(context)
            .logEvent(ReportingEvent.SENT_FEEDBACK.name, bundle)
        Answers.getInstance()
            .logCustom(
                CustomEvent(ReportingEvent.SENT_FEEDBACK.name)
                    .putCustomAttribute(ReportingParam.DATE.name, getCurrentDate())
                    .putCustomAttribute(ReportingParam.COUNTRY.name, context.getCountryCode())
            )
    }

    fun logViewWidget(context: Context) {
        if (BuildConfig.DEBUG) {
            return
        }
        val bundle = getDefaultBundle()
        FirebaseAnalytics.getInstance(context)
            .logEvent(ReportingEvent.VIEW_WIDGET.name, bundle)
        Answers.getInstance()
            .logCustom(
                CustomEvent(ReportingEvent.VIEW_WIDGET.name)
                    .putCustomAttribute(ReportingParam.DATE.name, getCurrentDate())
                    .putCustomAttribute(ReportingParam.COUNTRY.name, context.getCountryCode())
            )
    }

    fun logInstallWidget(context: Context) {
        if (BuildConfig.DEBUG) {
            return
        }
        val bundle = getDefaultBundle()
        FirebaseAnalytics.getInstance(context)
            .logEvent(ReportingEvent.INSTALL_WIDGET.name, bundle)
        Answers.getInstance()
            .logCustom(
                CustomEvent(ReportingEvent.INSTALL_WIDGET.name)
                    .putCustomAttribute(ReportingParam.DATE.name, getCurrentDate())
                    .putCustomAttribute(ReportingParam.COUNTRY.name, context.getCountryCode())
            )
    }

    fun logUninstallWidget(context: Context) {
        if (BuildConfig.DEBUG) {
            return
        }
        val bundle = getDefaultBundle()
        FirebaseAnalytics.getInstance(context)
            .logEvent(ReportingEvent.REMOVE_WIDGET.name, bundle)
        Answers.getInstance()
            .logCustom(
                CustomEvent(ReportingEvent.REMOVE_WIDGET.name)
                    .putCustomAttribute(ReportingParam.DATE.name, getCurrentDate())
                    .putCustomAttribute(ReportingParam.COUNTRY.name, context.getCountryCode())
            )
    }

    private fun getDefaultBundle() = bundleOf(
        ReportingParam.DATE.name to getCurrentDate(),
        ReportingParam.LANGUAGE.name to Locale.getDefault().displayLanguage
    )
}