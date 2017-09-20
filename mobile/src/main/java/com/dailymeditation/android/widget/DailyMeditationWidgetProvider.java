package com.dailymeditation.android.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.dailymeditation.android.activities.MainActivity;
import com.dailymeditation.android.service.UpdateWidgetService;
import com.dailymeditation.android.utils.firebase.AnalyticsUtils;

/**
 * Created with <3 by liacob & <Pi> on 18-Sep-17.
 */

public class DailyMeditationWidgetProvider extends AppWidgetProvider {

    public static final String OPEN_MAIN_ACTIVITY = "open_activity_action";
    public static final String OPEN_SHARE_INTENT = "open_share_intent";
    private static final long UPDATE_INTERVAL_MILLIS = 1000 * 60 * 20;
    private PendingIntent pendingIntent;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent i = new Intent(context, UpdateWidgetService.class);

        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        if (manager != null) {
            manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), UPDATE_INTERVAL_MILLIS, pendingIntent);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction() == null) {
            return;
        }
        Intent mainActivityIntent = new Intent(context, MainActivity.class);

        switch (intent.getAction()) {
            case OPEN_SHARE_INTENT:
                mainActivityIntent.putExtra(MainActivity.OPEN_SHARE_DIALOG, true);
            case OPEN_MAIN_ACTIVITY:
                mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainActivityIntent);
                AnalyticsUtils.logWidgetViewAll(context);
                break;
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AnalyticsUtils.logWidgetInstalled(context);
        Answers.getInstance().logCustom(new CustomEvent("Widget Created"));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AnalyticsUtils.logWidgetDisabled(context);
        Answers.getInstance().logCustom(new CustomEvent("Widget Removed"));
    }
}
