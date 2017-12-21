package com.dailymeditation.android.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

import com.dailymeditation.android.DailyMeditation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created with <3 by liacob & <Pi> on 6/30/2017.
 */

public class Utils {

    private static final String TAG = "DAILY_MEDITATION_TAG";

    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public static String getSimpleDate(String pubDate) {
        try {
            String pattern = "EEE, d MMM yyyy HH:mm:ss Z";
            SimpleDateFormat parseFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
            Date date = parseFormat.parse(pubDate);
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
            return format.format(date);
        } catch (Exception e) {
            LogUtils.logE(TAG, e);
            return "";
        }
    }

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    @NonNull
    public static String getCountryCode() {
        TelephonyManager tm = (TelephonyManager) DailyMeditation.getAppContext().getSystemService(TELEPHONY_SERVICE);
        return tm != null ? tm.getNetworkCountryIso() : "";
    }

}