package com.dailymeditation.android.utils;

import android.content.Context;

import com.dailymeditation.android.BuildConfig;
import com.dailymeditation.android.utils.firebase.AnalyticsUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created with <3 by liacob & <Pi> on 07-Sep-17.
 */

public class AdUtils {

    public static InterstitialAd getInterstitialAd(Context context) {
        InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId("ca-app-pub-1064911163417192/5620620596");
        interstitialAd.loadAd(getAdRequest());
        interstitialAd.setAdListener(new AdListener(context, interstitialAd));
        return interstitialAd;
    }

    public static AdRequest getAdRequest() {
        AdRequest.Builder adRequest = new AdRequest.Builder();

        if (BuildConfig.DEBUG) {
            adRequest.addTestDevice("A442C9EC329E172A98ECDAF392AF7300")
                    .addTestDevice("5B7EA98EE358FD317AED34323FFAFC71");
        }

        return adRequest.build();
    }

    public static class AdListener extends com.google.android.gms.ads.AdListener {
        private final Context context;
        private final InterstitialAd interstitialAd;

        AdListener(Context context, InterstitialAd interstitialAd) {
            this.context = context;
            this.interstitialAd = interstitialAd;
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            super.onAdFailedToLoad(errorCode);
            String errorName;
            switch (errorCode) {
                case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                    errorName = "ERROR_CODE_INTERNAL_ERROR";
                    break;
                case AdRequest.ERROR_CODE_INVALID_REQUEST:
                    errorName = "ERROR_CODE_INVALID_REQUEST";
                    break;
                case AdRequest.ERROR_CODE_NETWORK_ERROR:
                    errorName = "ERROR_CODE_NETWORK_ERROR";
                    break;
                case AdRequest.ERROR_CODE_NO_FILL:
                    errorName = "ERROR_CODE_NO_FILL";
                    break;
                default:
                    errorName = "";
                    break;
            }
            AnalyticsUtils.logInterstitialFailed(context, errorName);
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            AnalyticsUtils.logInterstitialDisplayed(context);
        }

        @Override
        public void onAdClicked() {
            super.onAdClicked();
            AnalyticsUtils.logInterstitialClick(context);
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            interstitialAd.loadAd(AdUtils.getAdRequest());
        }
    }
}
