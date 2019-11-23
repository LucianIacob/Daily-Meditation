package com.dailymeditation.android.utils

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import com.dailymeditation.android.DailyMeditation
import com.dailymeditation.android.reporting.ReportingManager
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {

        private const val REPORTING_DATE_FORMAT = "dd/MM/yy HH:mm:ss"
        private const val RSS_DATE_PATTERN = "EEE, d MMM yyyy HH:mm:ss Z"
        private const val APP_DATE_PATTERN = "EEE, d MMM yyyy"
        private const val EMPTY_STRING = ""

        val isNetworkAvailable: Boolean
            get() {
                val connectivityManager =
                    DailyMeditation.appContext?.getSystemService(Context.CONNECTIVITY_SERVICE)
                return if (connectivityManager is ConnectivityManager) {
                    val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
                    networkInfo?.isConnected ?: false
                } else false
            }

        fun createConnectivityChangeIntent(): IntentFilter {
            val filter = IntentFilter()
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
            return filter
        }

        fun getSimpleDate(pubDate: String): String {
            return try {
                val rssFormat = SimpleDateFormat(
                    RSS_DATE_PATTERN,
                    Locale.ENGLISH
                )
                val appFormat = SimpleDateFormat(
                    APP_DATE_PATTERN,
                    Locale.getDefault()
                )
                val date = rssFormat.parse(pubDate)
                if (date != null) appFormat.format(date) else ""
            } catch (e: ParseException) {
                ReportingManager.logErrorParsingDate(pubDate, e)
                EMPTY_STRING
            }
        }

        val currentDate: String
            get() {
                val dateFormat: DateFormat = SimpleDateFormat(
                    REPORTING_DATE_FORMAT,
                    Locale.US
                )
                return dateFormat.format(Date())
            }

        val countryCode: String =
            (DailyMeditation
                .appContext
                ?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    ).networkCountryIso
    }
}