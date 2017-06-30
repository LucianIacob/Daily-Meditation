package com.android.dailymeditation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rejasupotaro.asyncrssclient.AsyncRssClient;
import rejasupotaro.asyncrssclient.AsyncRssResponseHandler;
import rejasupotaro.asyncrssclient.RssFeed;
import rejasupotaro.asyncrssclient.RssItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AsyncRssClient mRssClient;

    @BindView(R.id.verse)
    TextView mVerseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRssClient = new AsyncRssClient();

        readVerse();
    }

    private void readVerse() {
        mRssClient.read(getString(R.string.verse_url), new AsyncRssResponseHandler() {
            @Override
            public void onSuccess(RssFeed rssFeed) {
                RssItem rssItem = rssFeed.getRssItems().get(0);
                mVerseTextView.setText(Html.fromHtml(rssItem.getDescription(), null, null));
                Log.i(TAG, "verse: " + rssItem.getTitle());
                Log.i(TAG, "verse: " + rssItem.getPubDate());
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                mVerseTextView.setText(getString(R.string.error_occurred));
            }
        });
    }
}
