package com.dailymeditation.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.dailymeditation.android.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rejasupotaro.asyncrssclient.AsyncRssClient;
import rejasupotaro.asyncrssclient.AsyncRssResponseHandler;
import rejasupotaro.asyncrssclient.RssFeed;
import rejasupotaro.asyncrssclient.RssItem;

public class MainActivity extends AppCompatActivity {

    private AsyncRssClient mRssClient;

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

    private int mNumberOfTries = 0;
    private boolean mVerseLoadedSuccessfully = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRssClient = new AsyncRssClient();
        initAds();
        readVerse();
        setShareButton();
    }

    private void initAds() {
        MobileAds.initialize(this, "ca-app-pub-1064911163417192/2951545067");
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("5B7EA98EE358FD317AED34323FFAFC71").build();
        mAdView.loadAd(adRequest);
    }

    private void readVerse() {
        mLoadingSpinner.setVisibility(View.VISIBLE);
        mVerseTextView.setVisibility(View.GONE);
        mVerseTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mRssClient.read(getString(R.string.verse_url), new AsyncRssResponseHandler() {
            @Override
            public void onSuccess(RssFeed rssFeed) {
                RssItem rssItem = rssFeed.getRssItems().get(0);
                mVerseTextView.setText(Html.fromHtml(rssItem.getDescription(), null, null));
                mVersePath.setText(rssItem.getTitle());
                try {  //Fri, 30 Jun 2017 00:00:00 -0600
                    SimpleDateFormat parseFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                    Date date = parseFormat.parse(rssItem.getPubDate());
                    SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH);
                    mPubDate.setText(format.format(date));
                } catch (Exception ignored) {
                }
                mNumberOfTries = 0;
                mLoadingSpinner.setVisibility(View.GONE);
                mVerseTextView.setVisibility(View.VISIBLE);
                mVerseLoadedSuccessfully = true;
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                mLoadingSpinner.setVisibility(View.GONE);
                mVerseTextView.setVisibility(View.VISIBLE);
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

    private void setShareButton() {
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mVerseTextView.getText().toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_verse)));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, Utils.createConnectivityChangeIntent());
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mVerseLoadedSuccessfully && Utils.isNetworkAvailable(context)) {
                readVerse();
            }
        }
    };

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
