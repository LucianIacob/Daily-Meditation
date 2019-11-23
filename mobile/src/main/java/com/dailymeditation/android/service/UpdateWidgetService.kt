package com.dailymeditation.android.service

import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import com.crashlytics.android.Crashlytics
import com.dailymeditation.android.R
import com.dailymeditation.android.reporting.ReportingManager
import com.dailymeditation.android.utils.fromHtml
import com.dailymeditation.android.utils.getSimpleDate
import com.dailymeditation.android.utils.networkAvailable
import com.dailymeditation.android.widget.DailyMeditationWidgetProvider
import com.prof.rssparser.Article
import com.prof.rssparser.Parser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class UpdateWidgetService : Service() {

    private var mViews: RemoteViews? = null
    private var mWidgetManager: AppWidgetManager? = null
    private var mComponentName: ComponentName? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mViews = RemoteViews(packageName, R.layout.widget_layout)
        mWidgetManager = AppWidgetManager.getInstance(this)
        mComponentName = ComponentName(this, DailyMeditationWidgetProvider::class.java)
        setClickListener()
        readVerse()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setClickListener() {
        val viewAllIntent = getPendingSelfIntent(
            applicationContext,
            DailyMeditationWidgetProvider.OPEN_MAIN_ACTIVITY
        )
        val shareIntent = getPendingSelfIntent(
            applicationContext,
            DailyMeditationWidgetProvider.OPEN_SHARE_INTENT
        )
        mViews?.run {
            setOnClickPendingIntent(R.id.view_all, viewAllIntent)
            setOnClickPendingIntent(R.id.widget_share, shareIntent)
            setOnClickPendingIntent(R.id.widget_layout, viewAllIntent)
        }
        mWidgetManager?.updateAppWidget(mComponentName, mViews)
    }

    private fun readVerse() {
        CoroutineScope(Dispatchers.Main + Job()).launch(Dispatchers.Main) {
            try {
                val passageOfTheDay = Parser()
                    .getArticles(getString(R.string.verse_url))
                    .firstOrNull()
                bindRssArticle(passageOfTheDay)
            } catch (throwable: Throwable) {
                mViews?.setTextViewText(
                    R.id.verse,
                    getString(if (networkAvailable()) R.string.error_occurred else R.string.network_error)
                )
                ReportingManager.logVerseLoaded(
                    this@UpdateWidgetService,
                    -1,
                    false,
                    throwable.message
                )
                Crashlytics.logException(throwable)
            }
        }
    }

    private fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, DailyMeditationWidgetProvider::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun bindRssArticle(passageOfTheDay: Article?) {
        mViews?.apply {
            setTextViewText(R.id.verse_path, passageOfTheDay?.title)
            setTextViewText(R.id.verse, passageOfTheDay?.description?.fromHtml())
            setTextViewText(R.id.verse_date, passageOfTheDay.getSimpleDate())
            setViewVisibility(R.id.view_all, View.VISIBLE)
        }
        mWidgetManager?.updateAppWidget(mComponentName, mViews)
        ReportingManager.logVerseLoaded(
            this,
            ReportingManager.STATUS_CODE_OK,
            true,
            Locale.getDefault().displayLanguage
        )
    }
}