package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.base.BaseActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.main.MainActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.onboarding.OnBoardingActivity
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ActivitySplashBinding
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.FIRST_INSTALL
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref.DataLocalManager

class SplashActivity: BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    override fun getColorState(): IntArray {
        return intArrayOf(Color.TRANSPARENT, Color.parseColor("#272727"))
    }

    override fun setUp() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        })

        Handler(Looper.getMainLooper()).postDelayed({
            if (!DataLocalManager.getFirstInstall(FIRST_INSTALL))
                startIntent(OnBoardingActivity::class.java.name, true)
            else startIntent(MainActivity::class.java.name, true)
        }, 2500)
    }
}