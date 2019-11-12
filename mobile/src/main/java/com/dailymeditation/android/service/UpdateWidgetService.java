package com.dailymeditation.android.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.Html;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.dailymeditation.android.R;
import com.dailymeditation.android.reporting.ReportingManager;
import com.dailymeditation.android.utils.Utils;
import com.dailymeditation.android.widget.DailyMeditationWidgetProvider;

import org.apache.http.Header;

import java.util.Locale;

import rejasupotaro.asyncrssclient.AsyncRssClient;
import rejasupotaro.asyncrssclient.AsyncRssResponseHandler;
import rejasupotaro.asyncrssclient.RssFeed;
import rejasupotaro.asyncrssclient.RssItem;

import static com.dailymeditation.android.widget.DailyMeditationWidgetProvider.OPEN_MAIN_ACTIVITY;
import static com.dailymeditation.android.widget.DailyMeditationWidgetProvider.OPEN_SHARE_INTENT;

/**
 * Created with <3 by liacob & <Pi> on 18-Sep-17.
 */

public class UpdateWidgetService extends Service implements AsyncRssResponseHandler {
    private AsyncRssClient mRssClient;
    private RemoteViews mViews;
    private AppWidgetManager mWidgetManager;
    private ComponentName mComponentName;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mRssClient = new AsyncRssClient();
        mViews = new RemoteViews(getPackageName(), R.layout.widget_layout);
        mWidgetManager = AppWidgetManager.getInstance(this);
        mComponentName = new ComponentName(this, DailyMeditationWidgetProvider.class);
        setClickListener();
        readVerse();
        return super.onStartCommand(intent, flags, startId);
    }

    private void setClickListener() {
        PendingIntent viewAllIntent = getPendingSelfIntent(getApplicationContext(), OPEN_MAIN_ACTIVITY);
        PendingIntent shareIntent = getPendingSelfIntent(getApplicationContext(), OPEN_SHARE_INTENT);
        mViews.setOnClickPendingIntent(R.id.view_all, viewAllIntent);
        mViews.setOnClickPendingIntent(R.id.widget_share, shareIntent);
        mViews.setOnClickPendingIntent(R.id.widget_layout, viewAllIntent);
        mWidgetManager.updateAppWidget(mComponentName, mViews);
    }

    private void readVerse() {
        mRssClient.read(getString(R.string.verse_url), this);
    }

    private PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, DailyMeditationWidgetProvider.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onSuccess(@NonNull RssFeed rssFeed) {
        RssItem rssItem = rssFeed.getRssItems().get(0);
        mViews.setTextViewText(R.id.verse_path, rssItem.getTitle());
        mViews.setTextViewText(R.id.verse, Html.fromHtml(rssItem.getDescription(), null, null));
        mViews.setTextViewText(R.id.verse_date, Utils.getSimpleDate(rssItem.getPubDate()));
        mViews.setViewVisibility(R.id.view_all, View.VISIBLE);
        mWidgetManager.updateAppWidget(mComponentName, mViews);
        ReportingManager.logVerseLoaded(
                this,
                ReportingManager.STATUS_CODE_OK,
                true,
                Locale.getDefault().getDisplayLanguage());
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, @NonNull Throwable throwable) {
        mViews.setTextViewText(R.id.verse, getString(Utils.isNetworkAvailable()
                ? R.string.error_occurred
                : R.string.network_error));
        ReportingManager.logVerseLoaded(
                this,
                i,
                false,
                throwable.getMessage());
    }
}