package com.dailymeditation.android.utils;

import com.dailymeditation.android.reporting.ReportingEvent;

import org.jetbrains.annotations.Contract;

/**
 * Created by Lucian Iacob.
 * Cluj-Napoca, 22 December, 2017.
 */

public enum AdType {

    INTERSTITIAL(
            ReportingEvent.OPEN_INTERSTITIAL,
            ReportingEvent.CLICK_INTERSTITIAL,
            ReportingEvent.IMPRESSION_INTERSTITIAL,
            ReportingEvent.ERROR_INTERSTITIAL),
    BANNER(
            ReportingEvent.OPEN_BANNER,
            ReportingEvent.CLICK_BANNER,
            ReportingEvent.IMPRESSION_BANNER,
            ReportingEvent.ERROR_BANNER);

    private final ReportingEvent mOpenEvent;
    private final ReportingEvent mClickEvent;
    private final ReportingEvent mImpressionEvent;
    private final ReportingEvent mErrorEvent;

    AdType(ReportingEvent open,
           ReportingEvent click,
           ReportingEvent impression,
           ReportingEvent error) {
        this.mOpenEvent = open;
        this.mClickEvent = click;
        this.mImpressionEvent = impression;
        this.mErrorEvent = error;
    }

    @Contract(pure = true)
    public ReportingEvent getOpenEvent() {
        return mOpenEvent;
    }

    @Contract(pure = true)
    public ReportingEvent getClickEvent() {
        return mClickEvent;
    }

    @Contract(pure = true)
    public ReportingEvent getImpressionEvent() {
        return mImpressionEvent;
    }

    @Contract(pure = true)
    public ReportingEvent getErrorEvent() {
        return mErrorEvent;
    }
}
