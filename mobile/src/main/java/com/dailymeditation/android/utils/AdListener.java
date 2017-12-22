package com.dailymeditation.android.utils;

import com.dailymeditation.android.reporting.AdRequestError;
import com.dailymeditation.android.reporting.ReportingManager;
import com.google.android.gms.ads.AdRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucian Iacob.
 * Cluj-Napoca, 22 December, 2017.
 */

class AdListener extends com.google.android.gms.ads.AdListener {

    private static final Map<Integer, AdRequestError> mAdErrors = new HashMap<Integer, AdRequestError>() {{
        put(AdRequest.ERROR_CODE_INTERNAL_ERROR, AdRequestError.INTERNAL_ERROR);
        put(AdRequest.ERROR_CODE_INVALID_REQUEST, AdRequestError.INVALID_REQUEST);
        put(AdRequest.ERROR_CODE_NETWORK_ERROR, AdRequestError.NETWORK_ERROR);
        put(AdRequest.ERROR_CODE_NO_FILL, AdRequestError.NO_FILL);
    }};

    private final AdType mAdType;

    AdListener(AdType adType) {
        mAdType = adType;
    }

    @Override
    public void onAdFailedToLoad(int i) {
        ReportingManager.logErrorAd(mAdType, mAdErrors.get(i));
    }

    @Override
    public void onAdOpened() {
        ReportingManager.logShowAd(mAdType);
    }

    @Override
    public void onAdClicked() {
        ReportingManager.logClickAd(mAdType);
    }

    @Override
    public void onAdImpression() {
        ReportingManager.logImpressionAd(mAdType);
    }
}
