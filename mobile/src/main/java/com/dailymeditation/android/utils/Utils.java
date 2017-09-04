package com.dailymeditation.android.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created with <3 by liacob & <Pi> on 6/30/2017.
 */

public class Utils {

    private static final String TAG = "DAILY_MEDITATION_TAG";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static IntentFilter createConnectivityChangeIntent() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        return filter;
    }

    public static String getSimpleDate(String pubDate) {
        try {  //Fri, 30 Jun 2017 00:00:00 -0600
            SimpleDateFormat parseFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            Date date = parseFormat.parse(pubDate);
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH);
            return format.format(date);
        } catch (Exception e) {
            LogUtils.logE(TAG, e);
            return "";
        }
    }
}