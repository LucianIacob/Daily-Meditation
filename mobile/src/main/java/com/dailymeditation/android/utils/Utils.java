package com.dailymeditation.android.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;

import com.dailymeditation.android.DailyMeditation;
import com.dailymeditation.android.reporting.ReportingManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created with <3 by liacob & <Pi> on 6/30/2017.
 */

public class Utils {

    private static final String REPORTING_DATE_FORMAT = "dd/MM/yy HH:mm:ss";
    private static final String RSS_DATE_PATTERN = "EEE, d MMM yyyy HH:mm:ss Z";
    private static final String APP_DATE_PATTERN = "EEE, d MMM yyyy";
    private static final String EMPTY_STRING = "";

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) DailyMeditation
                .getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @NonNull
    public static IntentFilter createConnectivityChangeIntent() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        return filter;
    }

    public static String getSimpleDate(@NonNull String pubDate) {
        try {
            SimpleDateFormat rssFormat = new SimpleDateFormat(RSS_DATE_PATTERN, Locale.ENGLISH);
            SimpleDateFormat appFormat = new SimpleDateFormat(APP_DATE_PATTERN, Locale.getDefault());
            Date date = rssFormat.parse(pubDate);
            if (date != null) return appFormat.format(date);
            else return "";
        } catch (ParseException e) {
            ReportingManager.logErrorParsingDate(pubDate, e);
            return EMPTY_STRING;
        }
    }

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat(REPORTING_DATE_FORMAT, Locale.US);
        return dateFormat.format(new Date());
    }

    @NonNull
    public static String getCountryCode() {
        TelephonyManager tm = (TelephonyManager) DailyMeditation
                .getAppContext()
                .getSystemService(TELEPHONY_SERVICE);
        return tm != null ? tm.getNetworkCountryIso() : EMPTY_STRING;
    }

}