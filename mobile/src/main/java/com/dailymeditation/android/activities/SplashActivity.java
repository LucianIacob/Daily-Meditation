package com.dailymeditation.android.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.dailymeditation.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 1000;

    @BindView(R.id.splash_icon)
    ImageView mSplashIcon;
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mUnbinder = ButterKnife.bind(this);
        init();
        gotoNextScreen();
    }

    private void init() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Drawable mDrawable = getResources().getDrawable(R.mipmap.ic_launcher);
        mDrawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.splash_icon_color), PorterDuff.Mode.MULTIPLY));
        mSplashIcon.setImageDrawable(mDrawable);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void gotoNextScreen() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
