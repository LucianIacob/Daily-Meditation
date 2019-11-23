package com.dailymeditation.android.utils

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import androidx.core.text.HtmlCompat
import com.prof.rssparser.Article
import java.text.SimpleDateFormat
import java.util.*

fun Context.networkAvailable(): Boolean =
    (getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)
        ?.activeNetworkInfo
        ?.isConnected
        ?: false

fun Boolean?.takeIfTrue() = this?.takeIf { it }
fun Boolean?.takeIfFalse() = this?.takeUnless { it }

fun Context.getCountryCode() =
    (getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)?.networkCountryIso

fun String?.fromHtml() = this?.let { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY) }

fun Article?.getSimpleDate(): String {
    val rssDatePattern = "EEE, d MMM yyyy HH:mm:ss Z"
    val appDatePattern = "EEE, d MMM yyyy"

    val rssFormat = SimpleDateFormat(rssDatePattern, Locale.ENGLISH)
    val appFormat = SimpleDateFormat(appDatePattern, Locale.getDefault())

    this?.pubDate?.let { dateString ->
        rssFormat.parse(dateString)?.let { return appFormat.format(it) }
    }
    return ""
}

fun getCurrentDate(): String {
    return SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.US).format(Date())
}