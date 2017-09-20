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

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.dailymeditation.android.R;
import com.dailymeditation.android.utils.Utils;
import com.dailymeditation.android.widget.DailyMeditationWidgetProvider;

import org.apache.http.Header;

import rejasupotaro.asyncrssclient.AsyncRssClient;
import rejasupotaro.asyncrssclient.AsyncRssResponseHandler;
import rejasupotaro.asyncrssclient.RssFeed;
import rejasupotaro.asyncrssclient.RssItem;

/**
 * Created with <3 by liacob & <Pi> on 18-Sep-17.
 */

public class UpdateWidgetService extends Service {
    private AsyncRssClient mRssClient;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mRssClient = new AsyncRssClient();
        readVerse();
        return super.onStartCommand(intent, flags, startId);
    }

    private void readVerse() {
        final RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget_layout);
        final AppWidgetManager manager = AppWidgetManager.getInstance(UpdateWidgetService.this);
        final ComponentName theWidget = new ComponentName(UpdateWidgetService.this, DailyMeditationWidgetProvider.class);

        setClickListener(view, manager, theWidget);
        mRssClient.read(getString(R.string.verse_url), new AsyncRssResponseHandler() {
            @Override
            public void onSuccess(RssFeed rssFeed) {
                RssItem rssItem = rssFeed.getRssItems().get(0);
                view.setTextViewText(R.id.verse_path, rssItem.getTitle());
                view.setTextViewText(R.id.verse, Html.fromHtml(rssItem.getDescription(), null, null));
                view.setTextViewText(R.id.verse_date, Utils.getSimpleDate(rssItem.getPubDate()));
                view.setViewVisibility(R.id.view_all, View.VISIBLE);
                manager.updateAppWidget(theWidget, view);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                if (!Utils.isNetworkAvailable(UpdateWidgetService.this)) {
                    view.setTextViewText(R.id.verse, getString(R.string.network_error));
                } else {
                    view.setTextViewText(R.id.verse, getString(R.string.error_occurred));
                }
                Answers.getInstance().logCustom(new CustomEvent("Widget Failure").putCustomAttribute("reason", throwable.getMessage()));
            }
        });
    }

    private void setClickListener(RemoteViews view, AppWidgetManager manager, ComponentName theWidget) {
        PendingIntent viewAllIntent = getPendingSelfIntent(getApplicationContext(), DailyMeditationWidgetProvider.OPEN_MAIN_ACTIVITY);
        PendingIntent shareIntent = getPendingSelfIntent(getApplicationContext(), DailyMeditationWidgetProvider.OPEN_SHARE_INTENT);
        view.setOnClickPendingIntent(R.id.view_all, viewAllIntent);
        view.setOnClickPendingIntent(R.id.widget_share, shareIntent);
        view.setOnClickPendingIntent(R.id.widget_layout, viewAllIntent);
        manager.updateAppWidget(theWidget, view);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, DailyMeditationWidgetProvider.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}