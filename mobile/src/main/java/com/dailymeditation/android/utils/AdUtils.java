package com.dailymeditation.android.utils;

import android.support.annotation.NonNull;

import com.dailymeditation.android.BuildConfig;
import com.dailymeditation.android.DailyMeditation;
import com.dailymeditation.android.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with <3 by liacob & <Pi> on 07-Sep-17.
 */

public class AdUtils {

    public static final String ADS_APP_ID = "ca-app-pub-1064911163417192~1614412667";

    @SuppressWarnings("SpellCheckingInspection")
    private static final List<String> mTestDevices = new ArrayList<String>() {{
        add("A442C9EC329E172A98ECDAF392AF7300");
        add("5B7EA98EE358FD317AED34323FFAFC71");
        add("2799935429D564F95982EE1EA441DDF6");
    }};

    @NonNull
    public static InterstitialAd setupInterstitialAd(@NonNull InterstitialAd interstitialAd) {
        interstitialAd.setAdUnitId(DailyMeditation.getAppContext().getString(R.string.ad_interstitial_uid));
        interstitialAd.setAdListener(new AdListener(AdType.INTERSTITIAL));
        return interstitialAd;
    }

    @NonNull
    public static AdView setupBannerAd(@NonNull AdView bannerAd) {
        bannerAd.setAdListener(new AdListener(AdType.BANNER));
        return bannerAd;
    }

    @NonNull
    public static AdRequest getAdRequest() {
        AdRequest.Builder adRequest = new AdRequest.Builder();

        if (BuildConfig.DEBUG) {
            for (String testDevice : mTestDevices) {
                adRequest.addTestDevice(testDevice);
            }
        }

        return adRequest.build();
    }

}
