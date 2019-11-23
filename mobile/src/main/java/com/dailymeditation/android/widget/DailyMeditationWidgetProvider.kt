package com.dailymeditation.android.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.dailymeditation.android.activities.MainActivity
import com.dailymeditation.android.reporting.ReportingManager
import com.dailymeditation.android.service.UpdateWidgetService

class DailyMeditationWidgetProvider : AppWidgetProvider() {

    private var pendingIntent: PendingIntent? = null

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getService(
                context,
                0,
                Intent(context, UpdateWidgetService::class.java),
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }

        (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)
            ?.setRepeating(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime(),
                UPDATE_INTERVAL_MILLIS,
                pendingIntent
            )
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == null) {
            return
        }

        val mainActivityIntent = Intent(context, MainActivity::class.java)

        if (intent.action == OPEN_SHARE_INTENT) {
            mainActivityIntent.putExtra(MainActivity.OPEN_SHARE_DIALOG, true)
        }

        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mainActivityIntent)
        ReportingManager.logViewWidget(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        ReportingManager.logInstallWidget(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        ReportingManager.logUninstallWidget(context)
    }

    companion object {
        const val OPEN_MAIN_ACTIVITY = "open_activity_action"
        const val OPEN_SHARE_INTENT = "open_share_intent"
        private const val UPDATE_INTERVAL_MILLIS = 1000 * 60 * 20.toLong()
    }
}