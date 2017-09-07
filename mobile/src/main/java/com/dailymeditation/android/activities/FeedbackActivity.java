package com.dailymeditation.android.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.dailymeditation.android.R;
import com.dailymeditation.android.model.Feedback;
import com.dailymeditation.android.utils.Utils;
import com.dailymeditation.android.utils.firebase.AnalyticsUtils;
import com.dailymeditation.android.utils.firebase.DatabaseUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackActivity extends AppCompatActivity {

    @BindView(R.id.feedback_content)
    EditText mFeedbackContent;

    private MenuItem mSendFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeback);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mFeedbackContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        mSendFeedback = menu.findItem(R.id.action_send_feedback);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean enableSend = mFeedbackContent.getText().toString().trim().length() != 0;
        mSendFeedback.setEnabled(enableSend);
        mSendFeedback.setIcon(enableSend ? R.drawable.ic_send_black_24dp : R.drawable.ic_send_gray_24dp);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_send_feedback:
                sendFeedback();
                Toast.makeText(this, R.string.thanks_for_feedback, Toast.LENGTH_SHORT).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendFeedback() {
        Locale locale = getResources().getConfiguration().locale;
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String countryCode = tm != null ? tm.getNetworkCountryIso() : locale.getISO3Country();
        Feedback feedback = new Feedback(Utils.getCurrentDate(), countryCode, locale.getDisplayLanguage(), mFeedbackContent.getText().toString());
        DatabaseUtils.uploadFeedback(feedback);
        AnalyticsUtils.logSendFeedback(this);
    }
}
