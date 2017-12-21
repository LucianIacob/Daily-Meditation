package com.dailymeditation.android.reporting;

import android.support.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;

/**
 * Created by Lucian Iacob.
 * Cluj-Napoca, 21 December, 2017.
 */

public enum AdRequestError {

    INTERNAL_ERROR,
    INVALID_REQUEST,
    NETWORK_ERROR,
    NO_FILL;

    @NonNull
    public static AdRequestError getErrorNameByCode(int code) {
        switch (code) {
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                return INVALID_REQUEST;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                return NETWORK_ERROR;
            case AdRequest.ERROR_CODE_NO_FILL:
                return NO_FILL;
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
            default:
                return INTERNAL_ERROR;
        }
    }
}
