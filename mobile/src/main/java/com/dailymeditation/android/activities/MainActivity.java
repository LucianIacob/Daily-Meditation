package com.dailymeditation.android.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dailymeditation.android.R;
import com.dailymeditation.android.models.Passage;
import com.dailymeditation.android.storage.DatabaseController;
import com.dailymeditation.android.utils.AdUtils;
import com.dailymeditation.android.utils.Utils;
import com.dailymeditation.android.utils.firebase.AnalyticsUtils;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rejasupotaro.asyncrssclient.AsyncRssClient;
import rejasupotaro.asyncrssclient.AsyncRssResponseHandler;
import rejasupotaro.asyncrssclient.RssFeed;
import rejasupotaro.asyncrssclient.RssItem;

public class MainActivity extends AppCompatActivity {

    public static final String OPEN_SHARE_DIALOG = "open_share_dialog";

    @BindView(R.id.verse_header)
    TextView mVerseHeader;
    @BindView(R.id.verse)
    TextView mVerseTextView;
    @BindView(R.id.verse_path)
    TextView mVersePath;
    @BindView(R.id.verse_date)
    TextView mPubDate;
    @BindView(R.id.loading_spinner)
    View mLoadingSpinner;
    @BindView(R.id.adView)
    AdView mAdView;
    @BindView(R.id.share_button)
    TextView mShareButton;

    private InterstitialAd mInterstitialAd;
    private AsyncRssClient mRssClient;
    private int mNumberOfTries = 0;
    private boolean mVerseLoadedSuccessfully = false;
    private DatabaseController mDatabaseController;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mVerseLoadedSuccessfully && Utils.isNetworkAvailable(context)) {
                readVerse();
                mInterstitialAd = AdUtils.getInterstitialAd(MainActivity.this);
                mAdView.loadAd(AdUtils.getAdRequest());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setShareButton();
    }

    private void init() {
        ButterKnife.bind(this);
        mRssClient = new AsyncRssClient();
        MobileAds.initialize(this, getString(R.string.ad_application_code));
        mInterstitialAd = AdUtils.getInterstitialAd(this);
        mAdView.loadAd(AdUtils.getAdRequest());
        if (!Utils.isNetworkAvailable(MainActivity.this)) {
            mVerseTextView.setText(getString(R.string.network_error));
        }
        mDatabaseController = new DatabaseController(this);
    }

    private void readVerse() {
        setLoadingSpinner(true);
        mVerseTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mRssClient.read(getString(R.string.verse_url), new AsyncRssResponseHandler() {
            @Override
            public void onSuccess(RssFeed rssFeed) {
                RssItem rssItem = rssFeed.getRssItems().get(0);
                boolean isRoLanguage = Utils.isRoLanguage();
                mVerseTextView.setText(isRoLanguage ? rssItem.getTitle() : Html.fromHtml(rssItem.getDescription(), null, null));
                mVersePath.setText(isRoLanguage ? "" : rssItem.getTitle());
                mPubDate.setText(Utils.getSimpleDate(isRoLanguage, rssItem.getPubDate()));
                mNumberOfTries = 0;
                setLoadingSpinner(false);
                mVerseLoadedSuccessfully = true;
                Passage passage = new Passage(rssItem.getDescription(), rssItem.getTitle(), Utils.getSimpleDate(isRoLanguage, rssItem.getPubDate()));
                mDatabaseController.addPassage(passage);
                AnalyticsUtils.logVerseLoaded(MainActivity.this, 200, true, Locale.getDefault().getDisplayLanguage());
                if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(OPEN_SHARE_DIALOG, false)) {
                    shareVerse();
                    AnalyticsUtils.logWidgetShare(MainActivity.this);
                }
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                setLoadingSpinner(false);
                if (mNumberOfTries < 3 && Utils.isNetworkAvailable(MainActivity.this)) {
                    mNumberOfTries++;
                    readVerse();
                    AnalyticsUtils.logVerseLoaded(MainActivity.this, i, false, getString(R.string.retry_called));
                } else {
                    if (!Utils.isNetworkAvailable(MainActivity.this)) {
                        mVerseTextView.setText(getString(R.string.network_error));
                        AnalyticsUtils.logVerseLoaded(MainActivity.this, i, false, getString(R.string.no_network));
                    } else {
                        mVerseTextView.setText(getString(R.string.error_occurred));
                        mNumberOfTries = 0;
                        AnalyticsUtils.logVerseLoaded(MainActivity.this, i, false, getString(R.string.error_occurred) + throwable.getMessage());
                    }
                }
            }
        });
    }

    private void setLoadingSpinner(boolean visibility) {
        mLoadingSpinner.setVisibility(visibility ? View.VISIBLE : View.GONE);
        mVerseTextView.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }

    private void setShareButton() {
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareVerse();
            }
        });
    }

    private void shareVerse() {
        if (mVerseLoadedSuccessfully) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, mVerseTextView.getText().toString());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_verse)));
        } else {
            Toast.makeText(MainActivity.this, R.string.verse_not_loaded, Toast.LENGTH_LONG).show();
        }
        AnalyticsUtils.logShareClick(MainActivity.this, mVerseLoadedSuccessfully, Locale.getDefault().getDisplayLanguage());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_feedback:
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                startActivity(new Intent(this, FeedbackActivity.class));
                AnalyticsUtils.logFeedbackClick(this, mVerseLoadedSuccessfully, Locale.getDefault().getDisplayLanguage());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, Utils.createConnectivityChangeIntent());
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }
    }
}
