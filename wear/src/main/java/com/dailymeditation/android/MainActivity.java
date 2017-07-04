package com.dailymeditation.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dailymeditation.android.utils.WearUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rejasupotaro.asyncrssclient.AsyncRssClient;
import rejasupotaro.asyncrssclient.AsyncRssResponseHandler;
import rejasupotaro.asyncrssclient.RssFeed;
import rejasupotaro.asyncrssclient.RssItem;

public class MainActivity extends Activity {

    private TextView mVerseTextView;
    private AsyncRssClient mRssClient;
    private TextView mDateTextView;
    private TextView mVersePathTV;
    private TextView mShareButton;

    private int mNumberOfTries = 0;
    private boolean mVerseLoadedSuccessfully = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRssClient = new AsyncRssClient();
        final WatchViewStub stub = findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mDateTextView = stub.findViewById(R.id.verse_date);
                mVersePathTV = stub.findViewById(R.id.verse_path);
                mShareButton = stub.findViewById(R.id.share_button);
                mVerseTextView = stub.findViewById(R.id.text);
                readFeed();
                setListener();
            }
        });
    }

    private void readFeed() {
        mRssClient.read(getString(R.string.verse_url), new AsyncRssResponseHandler() {
            @Override
            public void onSuccess(RssFeed rssFeed) {
                RssItem rssItem = rssFeed.getRssItems().get(0);
                mVerseTextView.setText(Html.fromHtml(rssItem.getDescription(), null, null));
                mVersePathTV.setText(rssItem.getTitle());
                try {
                    SimpleDateFormat parseFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                    Date date = parseFormat.parse(rssItem.getPubDate());
                    SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH);
                    mDateTextView.setText(format.format(date));
                } catch (Exception ignored) {
                }
                mNumberOfTries = 0;
                mVerseLoadedSuccessfully = true;
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                if (mNumberOfTries < 3 && WearUtils.isNetworkAvailable(MainActivity.this)) {
                    mNumberOfTries++;
                    readFeed();
                } else {
                    if (!WearUtils.isNetworkAvailable(MainActivity.this)) {
                        mVerseTextView.setText(getString(R.string.network_error));
                    } else {
                        mVerseTextView.setText(getString(R.string.error_occurred));
                        mNumberOfTries = 0;
                    }
                }
            }
        });
    }

    private void setListener() {
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mVerseLoadedSuccessfully) {
                    Toast.makeText(MainActivity.this, R.string.verse_not_loaded, Toast.LENGTH_LONG).show();
                } else {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, mVerseTextView.getText().toString());
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_verse)));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, WearUtils.createConnectivityChangeIntent());
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mVerseLoadedSuccessfully && WearUtils.isNetworkAvailable(context)) {
                readFeed();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
