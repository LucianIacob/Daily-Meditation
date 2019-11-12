package com.dailymeditation.android.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity implements AsyncRssResponseHandler {

    public static final String OPEN_SHARE_DIALOG = "open_share_dialog";
    private static final int RSS_READ_MAX_ATTEMPTS = 3;

    TextView mVerseTextView;
    TextView mVersePath;
    TextView mPubDate;
    View mLoadingSpinner;
    TextView mShareButton;

    private AsyncRssClient mRssClient;
    private int mNumberOfTries = 0;
    private boolean mVerseLoadedSuccessfully = false;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mVerseLoadedSuccessfully && Utils.isNetworkAvailable()) {
                readVerse();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVerseTextView = findViewById(R.id.verse);
        mVersePath = findViewById(R.id.verse_path);
        mPubDate = findViewById(R.id.verse_date);
        mLoadingSpinner = findViewById(R.id.loading_spinner);
        mShareButton = findViewById(R.id.share_button);
        init();
        setShareButton();
    }

    private void init() {
        mRssClient = new AsyncRssClient();
        if (!Utils.isNetworkAvailable()) {
            mVerseTextView.setText(getString(R.string.network_error));
        }
    }

    private void readVerse() {
        setLoadingSpinner(true);
        if (mVerseTextView != null) {
            mVerseTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        mRssClient.read(getString(R.string.verse_url), this);
    }

    private void setLoadingSpinner(boolean visibility) {
        if (mLoadingSpinner != null && mVerseTextView != null) {
            mLoadingSpinner.setVisibility(visibility ? View.VISIBLE : View.GONE);
            mVerseTextView.setVisibility(visibility ? View.GONE : View.VISIBLE);
        }
    }

    private void setShareButton() {
        mShareButton.setOnClickListener(v -> shareVerse(MainActivity.class.getSimpleName()));
    }

    private void shareVerse(String location) {
        if (mVerseLoadedSuccessfully) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, mVerseTextView.getText().toString());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_verse)));
        } else {
            Toast.makeText(MainActivity.this, R.string.verse_not_loaded, Toast.LENGTH_LONG).show();
        }
        ReportingManager.logShareClick(
                MainActivity.this,
                mVerseLoadedSuccessfully,
                location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_feedback) {
            startActivity(new Intent(this, FeedbackActivity.class));
            ReportingManager.logOpenFeedback(
                    this,
                    mVerseLoadedSuccessfully);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, Utils.createConnectivityChangeIntent());
    }

    @Override
    public void onSuccess(@NonNull RssFeed rssFeed) {
        bindRssItem(rssFeed.getRssItems().get(0));
        mNumberOfTries = 0;
        setLoadingSpinner(false);
        mVerseLoadedSuccessfully = true;
        ReportingManager.logVerseLoaded(
                MainActivity.this,
                ReportingManager.STATUS_CODE_OK,
                true,
                Locale.getDefault().getDisplayLanguage()
        );
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(OPEN_SHARE_DIALOG, false)) {
            shareVerse(DailyMeditationWidgetProvider.class.getSimpleName());
        }
    }

    private void bindRssItem(@NonNull RssItem rssItem) {
        mVerseTextView.setText(Html.fromHtml(rssItem.getDescription(), null, null));
        mVersePath.setText(rssItem.getTitle());
        mPubDate.setText(Utils.getSimpleDate(rssItem.getPubDate()));
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, @NonNull Throwable throwable) {
        setLoadingSpinner(false);
        String reportDetails;
        if (mNumberOfTries < RSS_READ_MAX_ATTEMPTS && Utils.isNetworkAvailable()) {
            mNumberOfTries++;
            readVerse();
            reportDetails = getString(R.string.retry_called);
        } else {
            mNumberOfTries = 0;
            if (mVerseTextView != null) {
                mVerseTextView.setText(getString(Utils.isNetworkAvailable()
                        ? R.string.error_occurred
                        : R.string.network_error));
            }
            reportDetails = getString(R.string.error_occurred) + throwable.getMessage();
        }
        ReportingManager.logVerseLoaded(
                this,
                i,
                false,
                reportDetails);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
