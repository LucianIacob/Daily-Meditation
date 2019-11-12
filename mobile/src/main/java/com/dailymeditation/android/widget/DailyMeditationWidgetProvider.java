package com.dailymeditation.android.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.dailymeditation.android.activities.MainActivity;
import com.dailymeditation.android.reporting.ReportingManager;
import com.dailymeditation.android.service.UpdateWidgetService;

/**
 * Created with <3 by liacob & <Pi> on 18-Sep-17.
 */

public class DailyMeditationWidgetProvider extends AppWidgetProvider {

    public static final String OPEN_MAIN_ACTIVITY = "open_activity_action";
    public static final String OPEN_SHARE_INTENT = "open_share_intent";
    private static final long UPDATE_INTERVAL_MILLIS = 1000 * 60 * 20;
    private PendingIntent pendingIntent;

    @Override
    public void onUpdate(@NonNull Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
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
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
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
                ReportingManager.logViewWidget(context);
                break;
        }
    }

    @Override
    public void onEnabled(@NonNull Context context) {
        super.onEnabled(context);
        ReportingManager.logInstallWidget(context);
    }

    @Override
    public void onDisabled(@NonNull Context context) {
        super.onDisabled(context);
        ReportingManager.logUninstallWidget(context);
    }
}
