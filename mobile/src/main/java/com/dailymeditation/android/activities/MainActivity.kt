package com.dailymeditation.android.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.crashlytics.android.Crashlytics
import com.dailymeditation.android.R
import com.dailymeditation.android.activities.MainActivity
import com.dailymeditation.android.reporting.ReportingManager
import com.dailymeditation.android.utils.Utils
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

    private var mNumberOfTries = 0
    private var mVerseLoadedSuccessfully = false

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (!mVerseLoadedSuccessfully && Utils.isNetworkAvailable) {
                readVerse()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setShareButton()
    }

    private fun init() {
        if (!Utils.isNetworkAvailable) {
            verse?.text = getString(R.string.network_error)
        }
    }

    private fun readVerse() {
        setLoadingSpinner(true)
        verse?.movementMethod = LinkMovementMethod.getInstance()

        val viewModelJob = Job()
        val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        coroutineScope.launch(Dispatchers.Main) {
            try {
                with(Parser()) {
                    val passageOfTheDay = getArticles(getString(R.string.verse_url))
                        .firstOrNull()
                    showData(passageOfTheDay)
                }
            } catch (e: Throwable) {
                setLoadingSpinner(false)
                val reportDetails: String

                if (mNumberOfTries < RSS_READ_MAX_ATTEMPTS && Utils.isNetworkAvailable) {
                    mNumberOfTries++
                    readVerse()
                    reportDetails = getString(R.string.retry_called)
                } else {
                    mNumberOfTries = 0
                    verse?.text =
                        getString(if (Utils.isNetworkAvailable) R.string.error_occurred else R.string.network_error)
                    reportDetails = getString(R.string.error_occurred) + e.message
                }
                ReportingManager.logVerseLoaded(
                    this@MainActivity,
                    -1,
                    false,
                    reportDetails
                )
                e.printStackTrace()
                Crashlytics.logException(e)
            }
        }
    }

    private fun showData(passageOfTheDay: Article?) {
        passageOfTheDay?.apply {
            verse?.text = description?.let {
                HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
            verse_path?.text = title
            verse_date?.text = pubDate?.let { Utils.getSimpleDate(it) }

            mNumberOfTries = 0
            setLoadingSpinner(false)
            mVerseLoadedSuccessfully = true

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
        if (mVerseLoadedSuccessfully) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, verse?.text.toString())
            sendIntent.type = "text/plain"
            startActivity(
                Intent.createChooser(
                    sendIntent,
                    resources.getText(R.string.send_verse)
                )
            )
        } else {
            Toast.makeText(this@MainActivity, R.string.verse_not_loaded, Toast.LENGTH_LONG).show()
        }
        ReportingManager.logShareClick(
            this@MainActivity,
            mVerseLoadedSuccessfully,
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
            ReportingManager.logOpenFeedback(
                this,
                mVerseLoadedSuccessfully
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(
            broadcastReceiver,
            Utils.createConnectivityChangeIntent()
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