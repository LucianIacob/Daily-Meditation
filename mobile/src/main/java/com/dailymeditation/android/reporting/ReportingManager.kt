package com.dailymeditation.android.reporting

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.crashlytics.android.answers.CustomEvent
import com.crashlytics.android.answers.ShareEvent
import com.dailymeditation.android.BuildConfig
import com.dailymeditation.android.utils.Utils
import com.google.firebase.analytics.FirebaseAnalytics
import java.text.ParseException
import java.util.*

class ReportingManager {

    companion object {

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
            val bundle = defaultBundle
            bundle.putBoolean(ReportingParam.SUCCESS.name, isSuccessful)
            bundle.putInt(ReportingParam.REASON.name, statusCode)
            bundle.putString(ReportingParam.LANGUAGE.name, details)
            FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.LOAD_VERSE.name, bundle)
            Answers.getInstance().logCustom(
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
            val bundle = defaultBundle
            bundle.putBoolean(ReportingParam.SUCCESS.name, isSuccessfully)
            bundle.putString(ReportingParam.LOCATION.name, location)
            FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.CLICK_SHARE.name, bundle)
            Answers.getInstance().logShare(
                ShareEvent()
                    .putMethod(Locale.getDefault().displayLanguage)
                    .putContentType(Utils.countryCode)
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
            val bundle = defaultBundle
            bundle.putBoolean(ReportingParam.SUCCESS.name, isSuccessfully)
            FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.OPEN_FEEDBACK.name, bundle)
            Answers.getInstance().logContentView(
                ContentViewEvent()
                    .putContentType(Locale.getDefault().displayLanguage)
                    .putCustomAttribute(ReportingParam.SUCCESS.name, isSuccessfully.toString())
            )
        }

        fun logSentFeedback(context: Context) {
            if (BuildConfig.DEBUG) {
                return
            }
            val bundle = defaultBundle
            FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.SENT_FEEDBACK.name, bundle)
            Answers.getInstance().logCustom(
                CustomEvent(ReportingEvent.SENT_FEEDBACK.name)
                    .putCustomAttribute(
                        ReportingParam.DATE.name,
                        Utils.currentDate
                    )
                    .putCustomAttribute(
                        ReportingParam.COUNTRY.name,
                        Utils.countryCode
                    )
            )
        }

        fun logViewWidget(context: Context) {
            if (BuildConfig.DEBUG) {
                return
            }
            val bundle = defaultBundle
            FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.VIEW_WIDGET.name, bundle)
            Answers.getInstance().logCustom(
                CustomEvent(ReportingEvent.VIEW_WIDGET.name)
                    .putCustomAttribute(
                        ReportingParam.DATE.name,
                        Utils.currentDate
                    )
                    .putCustomAttribute(
                        ReportingParam.COUNTRY.name,
                        Utils.countryCode
                    )
            )
        }

        fun logInstallWidget(context: Context) {
            if (BuildConfig.DEBUG) {
                return
            }
            val bundle = defaultBundle
            FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.INSTALL_WIDGET.name, bundle)
            Answers.getInstance().logCustom(
                CustomEvent(ReportingEvent.INSTALL_WIDGET.name)
                    .putCustomAttribute(
                        ReportingParam.DATE.name,
                        Utils.currentDate
                    )
                    .putCustomAttribute(
                        ReportingParam.COUNTRY.name,
                        Utils.countryCode
                    )
            )
        }

        fun logUninstallWidget(context: Context) {
            if (BuildConfig.DEBUG) {
                return
            }
            val bundle = defaultBundle
            FirebaseAnalytics.getInstance(context)
                .logEvent(ReportingEvent.REMOVE_WIDGET.name, bundle)
            Answers.getInstance().logCustom(
                CustomEvent(ReportingEvent.REMOVE_WIDGET.name)
                    .putCustomAttribute(
                        ReportingParam.DATE.name,
                        Utils.currentDate
                    )
                    .putCustomAttribute(
                        ReportingParam.COUNTRY.name,
                        Utils.countryCode
                    )
            )
        }

        fun logErrorParsingDate(pubDate: String, e: ParseException) {
            if (BuildConfig.DEBUG) {
                return
            }
            Answers.getInstance().logCustom(
                CustomEvent(ReportingEvent.ERROR_DATE.name)
                    .putCustomAttribute(ReportingParam.ERROR.name, e.message)
                    .putCustomAttribute(ReportingParam.REASON.name, pubDate)
                    .putCustomAttribute(
                        ReportingParam.COUNTRY.name,
                        Utils.countryCode
                    )
            )
        }

        private val defaultBundle: Bundle
            get() {
                return bundleOf(
                    ReportingParam.DATE.name to Utils.currentDate,
                    ReportingParam.COUNTRY.name to Utils.countryCode,
                    ReportingParam.LANGUAGE.name to Locale.getDefault().displayLanguage
                )
            }
    }
}