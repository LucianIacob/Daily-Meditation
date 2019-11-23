package com.dailymeditation.android.activities

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.dailymeditation.android.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init()
        gotoNextScreen()
    }

    private fun init() {
        supportActionBar?.hide()
        ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, null)?.let {
            it.colorFilter = PorterDuffColorFilter(
                ResourcesCompat.getColor(resources, R.color.splash_icon_color, null),
                PorterDuff.Mode.MULTIPLY
            )
            splash_icon?.setImageDrawable(it)
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private fun gotoNextScreen() {
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }

    companion object {
        private const val SPLASH_DISPLAY_LENGTH = 1000
    }
}