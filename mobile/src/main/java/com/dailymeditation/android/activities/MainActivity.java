package com.dailymeditation.android.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dailymeditation.android.R;
import com.dailymeditation.android.reporting.ReportingManager;
import com.dailymeditation.android.utils.AdUtils;
import com.dailymeditation.android.utils.Utils;
import com.dailymeditation.android.widget.DailyMeditationWidgetProvider;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.apache.http.Header;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rejasupotaro.asyncrssclient.AsyncRssClient;
import rejasupotaro.asyncrssclient.AsyncRssResponseHandler;
import rejasupotaro.asyncrssclient.RssFeed;
import rejasupotaro.asyncrssclient.RssItem;

public class MainActivity extends AppCompatActivity implements AsyncRssResponseHandler {

    public static final String OPEN_SHARE_DIALOG = "open_share_dialog";
    private static final int AD_MOB_VERSION_CODE_ISSUE = Build.VERSION_CODES.O;
    private static final int RSS_READ_MAX_ATTEMPTS = 3;

    @BindView(R.id.verse) TextView mVerseTextView;
    @BindView(R.id.verse_path) TextView mVersePath;
    @BindView(R.id.verse_date) TextView mPubDate;
    @BindView(R.id.loading_spinner) View mLoadingSpinner;
    @BindView(R.id.banner_ad) AdView mBannerAd;
    @BindView(R.id.share_button) TextView mShareButton;

    private Unbinder mUnbinder;
    private InterstitialAd mInterstitialAd;
    private AsyncRssClient mRssClient;
    private int mNumberOfTries = 0;
    private boolean mVerseLoadedSuccessfully = false;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mVerseLoadedSuccessfully && Utils.isNetworkAvailable()) {
                readVerse();
                loadAds();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        loadAds();
        setShareButton();
    }

    private void init() {
        mUnbinder = ButterKnife.bind(this);
        mRssClient = new AsyncRssClient();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd = AdUtils.setupInterstitialAd(mInterstitialAd);
        mBannerAd = AdUtils.setupBannerAd(mBannerAd);
        if (!Utils.isNetworkAvailable()) {
            mVerseTextView.setText(getString(R.string.network_error));
        }
    }

    private void loadAds() {
        mInterstitialAd.loadAd(AdUtils.getAdRequest());
        mBannerAd.loadAd(AdUtils.getAdRequest());
    }

    private void readVerse() {
        setLoadingSpinner(true);
        mVerseTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mRssClient.read(getString(R.string.verse_url), this);
    }

    private void setLoadingSpinner(boolean visibility) {
        if (mLoadingSpinner != null) {
            mLoadingSpinner.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
        mVerseTextView.setVisibility(visibility ? View.GONE : View.VISIBLE);
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
        switch (item.getItemId()) {
            case R.id.action_feedback:
                if (mInterstitialAd.isLoaded() && Build.VERSION.SDK_INT != AD_MOB_VERSION_CODE_ISSUE) {
                    mInterstitialAd.show();
                }
                startActivity(new Intent(this, FeedbackActivity.class));
                ReportingManager.logOpenFeedback(
                        this,
                        mVerseLoadedSuccessfully);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, Utils.createConnectivityChangeIntent());
        mBannerAd.resume();
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
                Locale.getDefault().getDisplayLanguage());
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
            mVerseTextView.setText(getString(Utils.isNetworkAvailable()
                    ? R.string.error_occurred
                    : R.string.network_error));
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
        mBannerAd.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        if (mBannerAd != null) {
            mBannerAd.destroy();
        }
    }
}
