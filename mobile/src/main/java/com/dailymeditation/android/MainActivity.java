package com.dailymeditation.android;

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

import com.dailymeditation.android.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import butterknife.BindView;
import butterknife.ButterKnife;
import rejasupotaro.asyncrssclient.AsyncRssClient;
import rejasupotaro.asyncrssclient.AsyncRssResponseHandler;
import rejasupotaro.asyncrssclient.RssFeed;
import rejasupotaro.asyncrssclient.RssItem;

public class MainActivity extends AppCompatActivity {

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
    private AsyncRssClient mRssClient;
    private int mNumberOfTries = 0;
    private boolean mVerseLoadedSuccessfully = false;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mVerseLoadedSuccessfully && Utils.isNetworkAvailable(context)) {
                readVerse();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        readVerse();
        setShareButton();
    }

    private void init() {
        mRssClient = new AsyncRssClient();
        MobileAds.initialize(this, getString(R.string.ad_application_code));
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getString(R.string.test_device_id)).build();
        mAdView.loadAd(adRequest);
    }

    private void readVerse() {
        setLoadingSpinner(true);
        mVerseTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mRssClient.read(getString(R.string.verse_url), new AsyncRssResponseHandler() {
            @Override
            public void onSuccess(RssFeed rssFeed) {
                RssItem rssItem = rssFeed.getRssItems().get(0);
                mVerseTextView.setText(Html.fromHtml(rssItem.getDescription(), null, null));
                mVersePath.setText(rssItem.getTitle());
                mPubDate.setText(Utils.getSimpleDate(rssItem.getPubDate()));
                mNumberOfTries = 0;
                setLoadingSpinner(false);
                mVerseLoadedSuccessfully = true;
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                setLoadingSpinner(false);
                if (mNumberOfTries < 3 && Utils.isNetworkAvailable(MainActivity.this)) {
                    mNumberOfTries++;
                    readVerse();
                } else {
                    if (!Utils.isNetworkAvailable(MainActivity.this)) {
                        mVerseTextView.setText(getString(R.string.network_error));
                    } else {
                        mVerseTextView.setText(getString(R.string.error_occurred));
                        mNumberOfTries = 0;
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
                if (mVerseLoadedSuccessfully) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, mVerseTextView.getText().toString());
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_verse)));
                } else {
                    Toast.makeText(MainActivity.this, R.string.verse_not_loaded, Toast.LENGTH_LONG).show();
                }
            }
        });
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
