package com.dailymeditation.android.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.dailymeditation.android.R
import com.dailymeditation.android.reporting.ReportingManager
import com.dailymeditation.android.utils.*
import com.dailymeditation.android.widget.DailyMeditationWidgetProvider
import com.prof.rssparser.Article
import com.prof.rssparser.Parser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    private var numberOfTries = 0
    private var loadedSuccessfully = false

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (!loadedSuccessfully && context.networkAvailable()) {
                readVerse()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setShareButton()
        networkAvailable().takeIfFalse()?.let { verse?.text = getString(R.string.network_error) }
    }

    private fun readVerse() {
        setLoadingSpinner(true)
        verse?.movementMethod = LinkMovementMethod.getInstance()

        CoroutineScope(Dispatchers.Main + Job()).launch(Dispatchers.Main) {
            try {
                val passageOfTheDay = Parser()
                    .getArticles(getString(R.string.verse_url))
                    .firstOrNull()
                showData(passageOfTheDay)
            } catch (throwable: Throwable) {
                setLoadingSpinner(false)
                val reportDetails: String

                if (numberOfTries < RSS_READ_MAX_ATTEMPTS && networkAvailable()) {
                    numberOfTries++
                    readVerse()
                    reportDetails = getString(R.string.retry_called)
                } else {
                    numberOfTries = 0
                    verse?.text = getString(networkAvailable().takeIfTrue()
                        ?.let { R.string.error_occurred } ?: R.string.network_error)
                    reportDetails = getString(R.string.error_occurred) + throwable.message
                }
                ReportingManager.logVerseLoaded(
                    this@MainActivity,
                    -1,
                    false,
                    reportDetails
                )
                throwable.printStackTrace()
                Crashlytics.logException(throwable)
            }
        }
    }

    private fun showData(passageOfTheDay: Article?) {
        passageOfTheDay?.apply {
            verse?.text = description?.fromHtml()
            verse_path?.text = title
            verse_date?.text = getSimpleDate()

            numberOfTries = 0
            setLoadingSpinner(false)
            loadedSuccessfully = true

            ReportingManager.logVerseLoaded(
                this@MainActivity,
                ReportingManager.STATUS_CODE_OK,
                true,
                Locale.getDefault().displayLanguage
            )

            intent.extras?.run {
                if (getBoolean(OPEN_SHARE_DIALOG, false)) {
                    shareVerse(DailyMeditationWidgetProvider::class.java.simpleName)
                }
            }
        }
    }

    private fun setLoadingSpinner(visibility: Boolean) {
        loading_spinner?.visibility = if (visibility) View.VISIBLE else View.GONE
        verse?.visibility = if (visibility) View.GONE else View.VISIBLE
    }

    private fun setShareButton() {
        share_button?.setOnClickListener { shareVerse(MainActivity::class.java.simpleName) }
    }

    private fun shareVerse(location: String) {
        if (loadedSuccessfully) {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, verse?.text.toString())
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.send_verse)))
        } else {
            Toast.makeText(this@MainActivity, R.string.verse_not_loaded, Toast.LENGTH_LONG).show()
        }
        ReportingManager.logShareClick(
            this@MainActivity,
            loadedSuccessfully,
            location
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_feedback) {
            startActivity(Intent(this, FeedbackActivity::class.java))
            ReportingManager.logOpenFeedback(this, loadedSuccessfully)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(
            broadcastReceiver,
            IntentFilter().apply { addAction("android.net.conn.CONNECTIVITY_CHANGE") }
        )
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    companion object {
        const val OPEN_SHARE_DIALOG = "open_share_dialog"
        private const val RSS_READ_MAX_ATTEMPTS = 3
    }
}