package com.dailymeditation.android.activities

import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.trimmedLength
import androidx.core.widget.doOnTextChanged
import com.dailymeditation.android.R
import com.dailymeditation.android.reporting.ReportingManager
import com.dailymeditation.android.utils.getCurrentDate
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_feeback.*
import java.util.*

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feeback)
        init()
    }

    private fun init() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        feedback_content?.doOnTextChanged { _, _, _, _ -> invalidateOptionsMenu() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_feedback, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val enableSend = feedback_content
            ?.text
            ?.trimmedLength()
            ?.takeIf { it > 0 }
            ?.let { true } ?: false

        with(menu.findItem(R.id.action_send_feedback)) {
            isEnabled = enableSend
            setIcon(if (enableSend) R.drawable.ic_send_black_24dp else R.drawable.ic_send_gray_24dp)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_send_feedback -> {
                sendFeedback()
                Toast.makeText(this, R.string.thanks_for_feedback, Toast.LENGTH_SHORT).show()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun sendFeedback() {
        val feedback = Feedback(
            date = getCurrentDate(),
            country = (getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)?.networkCountryIso,
            language = Locale.getDefault().displayLanguage,
            feedback = feedback_content?.text.toString()
        )

        FirebaseDatabase.getInstance()
            .getReference(FEEDBACK_REFERENCE)
            .push()
            .setValue(feedback)
            .also { ReportingManager.logSentFeedback(this) }
    }

    companion object {
        private const val FEEDBACK_REFERENCE = "feedback_db"
    }

    private data class Feedback(
        val date: String?,
        val country: String?,
        val language: String?,
        val feedback: String?
    )
}